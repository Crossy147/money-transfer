package api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import service.BaseTest

import scala.concurrent.ExecutionContext

abstract class EndpointTest
    extends BaseTest
    with GenCodecMarshalling
    with ScalatestRouteTest {

  override implicit val executor = ExecutionContext.global
  val route: Route
}
