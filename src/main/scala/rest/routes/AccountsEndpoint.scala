package rest.routes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import model.dto.AccountCreationDTO
import model.{Account, AccountId}
import service.AccountService

import scala.concurrent.ExecutionContext
import scala.util.Success

class AccountsEndpoint(accountService: AccountService)(
    implicit val ec: ExecutionContext)
    extends HasRoute
    with Directives {

  override val route: Route = handleExceptions(ApiExceptionHandler.handler) {
    pathPrefix("accounts") {
      pathEndOrSingleSlash {
        get {
          onSuccess(accountService.getAll()) { accounts =>
            complete(StatusCodes.OK, accounts)
          }
        } ~
          post {
            entity(as[AccountCreationDTO]) { accountDTO =>
              onSuccess(accountService.create(Account.fromDTO(accountDTO))) {
                transactionId =>
                  complete(StatusCodes.Created, transactionId)
              }
            }
          }
      } ~
        pathPrefix(LongNumber) { id =>
          pathEndOrSingleSlash {
            get {
              onComplete(accountService.get(AccountId(id))) {
                case Success(Some(account)) =>
                  complete(account)
                case Success(None) =>
                  complete(StatusCodes.NotFound,
                           s"There is no account with id $id")
              }
            }
          }
        }
    }
  }
}
