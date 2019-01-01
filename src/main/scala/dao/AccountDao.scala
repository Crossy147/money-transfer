package dao

import db.Db
import exceptions.NegativeAccountBalanceException
import model.{Account, AccountId}
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}

class AccountDao(database: Db)(implicit executionContext: ExecutionContext) extends HasTableQuery {

  def getAll: Future[Seq[Account]] =
    database.instance.run(accounts.result)

  def getById(accountId: AccountId): Future[Option[Account]] =
    database.instance.run(accounts.filter(_.id === accountId).result.headOption)

  def create(account: Account): Future[AccountId] = {
    def validateBalance(): Unit = {
      if (account.balance < 0) throw new NegativeAccountBalanceException(account.balance)
    }

    for {
      _  <- Future(validateBalance())
      accountId <- database.instance.run(accounts returning accounts.map(_.id) += account)
    } yield accountId
  }
}

class AccountScheme(tag: Tag) extends Table[Account](tag, "ACCOUNTS") {
  def id = column[AccountId]("ACCOUNT_ID", O.PrimaryKey, O.AutoInc)
  def balance = column[BigDecimal]("BALANCE")
  def * = (balance, id.?) <> ((Account.apply _).tupled, Account.unapply)
}
