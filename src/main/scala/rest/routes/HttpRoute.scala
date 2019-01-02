package rest.routes

import akka.http.scaladsl.server.{Directives, Route}

import scala.concurrent.ExecutionContext

class HttpRoute(accountRoutes: AccountsEndpoint, transactionRoutes: TransactionsEndpoint)
               (implicit ec: ExecutionContext) extends HasRoute with Directives {
  override val route: Route = handleExceptions(ApiExceptionHandler.handler){
    accountRoutes.route ~ transactionRoutes.route
  }
}
