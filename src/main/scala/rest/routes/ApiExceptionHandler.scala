package rest.routes

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import com.typesafe.scalalogging.LazyLogging
import exceptions.IllegalRestCallArgumentsException

object ApiExceptionHandler extends Directives with LazyLogging {
  val handler: ExceptionHandler = ExceptionHandler {
    case ex : IllegalRestCallArgumentsException =>
      logger.debug(s"Bad request resulted in exception ${ex.getMessage}")
      complete(HttpResponse(BadRequest, entity = ex.getMessage))
    case ex : Throwable =>
      logger.error(s"Internal server error with exception $ex")
      complete(HttpResponse(InternalServerError, entity = ex.getMessage))
  }
}
