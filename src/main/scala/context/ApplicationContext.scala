package context

import dao.{AccountDao, TransactionDao}
import db.Db
import rest.routes.{AccountsEndpoint, HttpRoute, TransactionsEndpoint}
import service.{AccountService, TransactionService}

import scala.concurrent.ExecutionContext

trait ApplicationContext {
  import com.softwaremill.macwire._

  implicit val ec = ExecutionContext.global

  lazy val configPath = "h2InMemory"
  lazy val db: Db = wire[Db]

  lazy val accountDao: AccountDao = wire[AccountDao]
  lazy val transactionDao: TransactionDao = wire[TransactionDao]

  lazy val accountService: AccountService = wire[AccountService]
  lazy val transactionService: TransactionService = wire[TransactionService]

  lazy val accountRoutes: AccountsEndpoint = wire[AccountsEndpoint]
  lazy val transactionRoutes: TransactionsEndpoint = wire[TransactionsEndpoint]

  lazy val httpRoute: HttpRoute = wire[HttpRoute]
}