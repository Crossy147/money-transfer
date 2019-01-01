package dao

import exceptions.AccountException
import model.{Account, AccountId, Transaction, TransactionId}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

trait TransactionHandler[+T <: TransactionId, +S <: NoStream, -E <: Effect] extends (() => DBIOAction[T, S, E]) {
  override def apply(): DBIOAction[T, S, E]
}
final class ProperTransaction(
    source: Account,
    target: Account,
    transaction: Transaction)(
                               transactions: TableQuery[TransactionScheme],
                               accounts: TableQuery[AccountScheme]
)(implicit ec: ExecutionContext) extends TransactionHandler[TransactionId, NoStream, Effect.Write] {

  private def updateWithBalance(account: Account, op: (BigDecimal, BigDecimal) => BigDecimal) = {
    accounts
      .filter(_.id === account.id)
      .map(_.balance)
      .update(op(account.balance, transaction.amount))
  }

  override def apply(): DBIOAction[TransactionId, NoStream, Effect.Write] = {
    for {
      _ <- updateWithBalance(source, _ - _)
      _ <- updateWithBalance(target, _ + _)
      transactionInsert <- transactions returning transactions.map(_.id) += transaction
    } yield transactionInsert
  }
}
abstract class InProperTransaction(exception: AccountException) extends TransactionHandler[Nothing, NoStream, Effect] {
  override def apply(): DBIOAction[Nothing, NoStream, Effect] = DBIOAction.failed(exception)
}

final class NonExistentSourceAccount(sourceId: AccountId)
    extends InProperTransaction(AccountException.NonExistentSourceAccountException(sourceId))

final class NonExistentTargetAccount(targetId: AccountId)
  extends InProperTransaction(AccountException.NonExistentTargetAccountException(targetId))

final class NonExistentSourceAndTargetAccount(sourceId: AccountId, targetId: AccountId)
    extends InProperTransaction(AccountException.NonExistentSourceAndTargetAccountException(sourceId, targetId))

final class NotEnoughFundsOnSourceAccount(sourceId: AccountId, amount: BigDecimal)
    extends InProperTransaction(AccountException.NotEnoughFundsException(sourceId, amount))
