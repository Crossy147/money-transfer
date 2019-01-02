package rest.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import model.Transaction
import model.dto.TransactionCreationDTO
import service.TransactionService

import scala.concurrent.ExecutionContext

class TransactionsEndpoint(transactionService: TransactionService)(implicit ec: ExecutionContext)
    extends HasRoute with Directives {

  override val route: Route = handleExceptions(ApiExceptionHandler.handler) {
    pathPrefix("transactions") {
      pathEndOrSingleSlash {
        get {
          onSuccess(transactionService.getAll()) { transactions =>
            complete(OK, transactions)
          }
        } ~ post {
          entity(as[TransactionCreationDTO]) { transactionDTO =>
            onSuccess(transactionService.create(Transaction.fromDto(transactionDTO))) { transactionId =>
              complete(Created, transactionId)
            }
          }
        }
      }
    }
  }
}
