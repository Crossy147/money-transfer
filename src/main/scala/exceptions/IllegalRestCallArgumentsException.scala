package exceptions

import model.AccountId

sealed abstract class IllegalRestCallArgumentsException(message: String) extends IllegalArgumentException(message)

sealed abstract class TransactionException(message: String) extends IllegalRestCallArgumentsException(message)
sealed abstract class PrerequisiteException(message: String) extends TransactionException(message)
object PrerequisiteException {
  case class NonPositiveAmountException(amount: BigDecimal) extends PrerequisiteException(
    s"Transaction amount must be positive, but was ${amount}"
  )
  case class SameSourceAndTargetAccountException(accountId: AccountId) extends PrerequisiteException(
    s"Transaction must be between 2 different account, but is from and to account with id ${accountId}"
  )
}
sealed abstract class AccountException(message: String) extends TransactionException(message)
object AccountException {
  case class NonExistentSourceAccountException(id: AccountId)
    extends AccountException(s"Source account with id ${id.value} does not exist")
  case class NonExistentTargetAccountException(id: AccountId)
    extends AccountException(s"Target account with id ${id.value} does not exist")
  case class NonExistentSourceAndTargetAccountException(source: AccountId, target: AccountId)
    extends AccountException(s"Source account with id ${source.value} and target account with id ${target.value} do not exist")
  case class NotEnoughFundsException(accountId: AccountId, requestedAmount: BigDecimal)
    extends AccountException(s"Account with id ${accountId.value} does not have enough funds to transfer $requestedAmount")
}

final case class NegativeAccountBalanceException(balance: BigDecimal)
  extends IllegalRestCallArgumentsException(s"Account balance must not be negative, but was ${balance}")