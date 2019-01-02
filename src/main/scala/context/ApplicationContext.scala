package context

import api.{AccountsEndpoint, HttpRoute, TransactionsEndpoint}
import dao.{AccountDao, TransactionDao}
import db.Db
import service.{AccountService, TransactionService}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

trait ApplicationContext {
  import com.softwaremill.macwire._

  implicit val executor: ExecutionContextExecutor = ExecutionContext.global

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
