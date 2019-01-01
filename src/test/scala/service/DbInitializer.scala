package service

import com.avsystem.commons.misc.Timestamp
import dao.HasTableQuery
import db.Db
import model.{Account, AccountId, Transaction}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

trait HasEntities[Entity] {
  val ids = 1 to 6
  val entities: Seq[Entity]
}

object AccountEntities extends HasEntities[Account] {
  override val entities: Seq[Account] = ids
    .map(it => Account(it * 100.00, Some(AccountId(it))))
}

object TransactionEntities extends HasEntities[Transaction] {
  override val entities: Seq[Transaction] = ids
    .map(it => Transaction(AccountId(it), AccountId((it + 1) % 6), it, Timestamp.now()))
}

class DbInitializer(db: Db) extends HasTableQuery {

  def addTransactions(): Future[Unit] =
    db.instance.run(DBIO.seq(transactions ++= TransactionEntities.entities))


  def initializeWithSampleAccounts(): Future[Unit] =
    db.instance.run(DBIO.seq(
      (accounts.schema ++ transactions.schema).create,
      accounts ++= AccountEntities.entities,
    ))
  def clean(): Future[Unit] =
    db.instance.run(DBIO.seq(accounts.schema.drop, transactions.schema.drop))
}

