import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import context.ApplicationContext
import utils.HttpConfig

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object ServerMain
    extends App
    with ApplicationContext
    with LazyLogging
    with HttpConfig {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val bindingFuture = {
    logger.info("Creating tables...")
    db.createTables()
    logger.info("Starting http endpoints...")
    val mainRoute: Route = httpRoute.route
    val bf = Http().bindAndHandle(mainRoute, httpHost, httpPort)
    logger.info(s"Server online at $httpHost:$httpPort")
    bf
  }

  locally {
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => {
        onShutdown()
      })
  }

  def onShutdown(): Unit = {
    logger.info("Closing db connection...")
    db.instance.close()
    logger.info("Terminating server...")
    system.terminate()
  }
}
