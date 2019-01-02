package dao

import com.avsystem.commons.misc.Timestamp
import com.typesafe.scalalogging.LazyLogging
import db.Db
import exceptions.PrerequisiteException
import model._
import slick.jdbc.H2Profile.api._
import slick.lifted.Tag

import scala.concurrent.{ExecutionContext, Future}

class TransactionDao(db: Db)(implicit ec: ExecutionContext) extends LazyLogging with HasTableQuery {


  def getAll(): Future[Seq[Transaction]] =
    db.instance.run(transactions.result)

  def get(transactionId: TransactionId): Future[Option[Transaction]] =
    db.instance.run(
      transactions.filter(_.id === transactionId).take(1).result.headOption)

  def getForAccount(accountId: AccountId): Future[Seq[Transaction]] = {
    db.instance.run(
      transactions.filter(transaction => transaction.from === accountId || transaction.to === accountId)
        .result
    )
  }

  def create(transaction: Transaction): Future[TransactionId] = {

    def verifyTransaction(): Future[Unit] = {
      if (transaction.from == transaction.to)
        Future.failed(PrerequisiteException.SameSourceAndTargetAccountException(transaction.from))
      else if (transaction.amount <= 0)
        Future.failed(PrerequisiteException.NonPositiveAmountException(transaction.amount))
      else Future.unit
    }

    def oneBy(fieldGetter: Transaction => AccountId) =
      accounts.filter(_.id === fieldGetter(transaction))
        .result
        .headOption

    val transferBetweenAccountsAction = (for {
      sourceAccount <- oneBy(_.from)
      targetAccount <- oneBy(_.to)
      handler <- (sourceAccount, targetAccount) match {
        case (Some(source), Some(target)) if source.balance >= transaction.amount =>
          new ProperTransaction(source, target, transaction)(transactions, accounts).apply()
        case (Some(_), Some(_)) =>
          new NotEnoughFundsOnSourceAccount(transaction.from, transaction.amount)()
        case (None, Some(_)) =>
          new NonExistentSourceAccount(transaction.from)()
        case (Some(_), None) =>
          new NonExistentTargetAccount(transaction.to)()
        case (None, None) =>
          new NonExistentSourceAndTargetAccount(transaction.from, transaction.to)()
      }
    } yield handler).transactionally

    for {
      _ <- verifyTransaction()
      result <- db.instance.run(transferBetweenAccountsAction)
    } yield result
  }
}

class TransactionScheme(tag: Tag) extends Table[Transaction](tag, "TRANSACTIONS") with HasTableQuery {
  implicit val TimestampMapper: BaseColumnType[Timestamp] =
    MappedColumnType.base[Timestamp, Long](_.millis, Timestamp(_))

  def id = column[TransactionId]("TRANSACTION_ID", O.PrimaryKey, O.AutoInc)
  def from = column[AccountId]("FROM_ACCOUNT")
  def to = column[AccountId]("TO_ACCOUNT")
  def amount = column[BigDecimal]("AMOUNT")
  def timestamp = column[Timestamp]("TIMESTAMP", O.Default(Timestamp.now()))

  def * = (from, to, amount, timestamp, id.?) <> ((Transaction.apply _).tupled, Transaction.unapply)

  // Foreign keys to account id for both from and to column can be added, but are not used for now.

}
