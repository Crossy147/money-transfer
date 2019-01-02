package rest.routes
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import model.dto.AccountCreationDTO
import model.{Account, AccountId}
import service.{AccountService, TransactionService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountsEndpoint(accountService: AccountService, transactionService: TransactionService)(implicit  ec: ExecutionContext)
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
                accountId =>
                  complete(StatusCodes.Created, accountId)
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
                  complete(StatusCodes.NotFound, s"There is no account with id $id")
                case Failure(ex) =>
                  ApiExceptionHandler.handler(ex)
              }
            }
          } ~ pathSuffix("transactions") {
            pathEndOrSingleSlash {
              get {
                onComplete(transactionService.getForAccount(AccountId(id))) {
                  transactions =>
                    complete(StatusCodes.OK, transactions)
                }
              }
            }
          }
        }
    }
  }
}
