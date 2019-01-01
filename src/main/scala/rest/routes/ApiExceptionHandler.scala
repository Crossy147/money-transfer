package rest.routes

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import com.typesafe.scalalogging.LazyLogging

object ApiExceptionHandler extends Directives with LazyLogging {
  val handler: ExceptionHandler = ExceptionHandler {
    case ex : Throwable =>
      logger.error(s"Internal server error with exception $ex")
      complete(HttpResponse(InternalServerError, entity = ex.getMessage))
  }
}
