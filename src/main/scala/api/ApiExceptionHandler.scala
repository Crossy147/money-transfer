package api

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import com.typesafe.scalalogging.LazyLogging
import exceptions.IllegalRestCallArgumentsException

import scala.util.control.NonFatal

object ApiExceptionHandler extends Directives with LazyLogging {
  val handler: ExceptionHandler = ExceptionHandler {
    case ex : IllegalRestCallArgumentsException =>
      logger.debug(s"Bad request resulted in exception ${ex.getMessage}")
      complete(HttpResponse(BadRequest, entity = ex.getMessage))
    case NonFatal(ex) =>
      logger.error(s"Internal server error with exception $ex")
      complete(HttpResponse(InternalServerError, entity = ex.getMessage))
  }
}
