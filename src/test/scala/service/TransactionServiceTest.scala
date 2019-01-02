package service

import com.typesafe.scalalogging.LazyLogging
import exceptions.{AccountException, PrerequisiteException, TransactionException}
import model.{Account, AccountId, Transaction, TransactionId}
import org.scalatest.Assertion
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.Future
import scala.reflect.ClassTag

/**
  * [[BaseTest]] populates account table with entities before each testcase.
  */
class TransactionServiceTest extends BaseTest with LazyLogging {
  import org.scalatest.concurrent.PatienceConfiguration._

  def throwsEx[EXCEPTION <: TransactionException: ClassTag](transaction: Transaction): Assertion =
    whenReady { for {
        ex <- transactionService.create(transaction).failed
        all <- transactionService.getAll()
      } yield {
        ex shouldBe an[EXCEPTION]
        all shouldBe empty
      }
    }(identity)


  "create" should "accept money transaction" in {
    val transaction = Transaction(AccountId(1), AccountId(2), 20.00)
    transactionService
      .create(transaction)
      .map(_ shouldBe TransactionId(1))
  }

  it should "not accept transaction with non-positive amount" in {
    throwsEx[PrerequisiteException.NonPositiveAmountException](Transaction(AccountId(1), AccountId(2), -10.0))
  }

  it should "not accept transaction with the same source and target" in {
    throwsEx[PrerequisiteException.SameSourceAndTargetAccountException](Transaction(AccountId(1), AccountId(1), 10.0))
  }

  it should "not accept transaction for incorrect source account" in {
    throwsEx[AccountException.NonExistentSourceAccountException](Transaction(AccountId(10), AccountId(1), 20.0))
  }

  it should "not accept a transaction for incorrect target account" in {
    throwsEx[AccountException.NonExistentTargetAccountException](Transaction(AccountId(1), AccountId(20), 10.0))
  }

  it should "not accept a transaction for incorrect  source and target accounts" in {
    throwsEx[AccountException.NonExistentSourceAndTargetAccountException](Transaction(AccountId(21), AccountId(20), 10.0))
  }

  it should "not accept a transaction when source account does not have enough founds" in {
    throwsEx[AccountException.NotEnoughFundsException](Transaction(AccountId(1), AccountId(2), 10000.0))
  }

  it should "affect accounts' balances" in {
    val initialBalance: BigDecimal = 100.00
    val amount: BigDecimal = 40.00
    whenReady(accountService.create(Account(initialBalance)) zip accountService.create(Account(initialBalance))) {
      case (sourceId, targetId) =>
        whenReady(transactionService.create(Transaction(sourceId, targetId, amount))) {
          id =>
            whenReady(for {
              newSourceAccount <- accountService.get(sourceId)
              newTargetAccount <- accountService.get(targetId)
              transactionId <- transactionService.get(id)
            } yield (newSourceAccount, newTargetAccount, transactionId)) {
              case (s, t, u) =>
                u.map(_.from) shouldBe s.flatMap(_.id)
                u.map(_.to) shouldBe t.flatMap(_.id)
                u.map(_.amount).value shouldBe amount
                s.map(_.balance).value shouldBe initialBalance - amount
                t.map(_.balance).value shouldBe initialBalance + amount
            }
        }
    }
  }

  it should "remain initial balances once followed by a reversed transfer" in {
    val initialBalance: BigDecimal = 100.00
    val amount: BigDecimal = 40.00

    whenReady(for {
      (source, target) <- accountService.create(Account(initialBalance)) zip accountService.create(Account(initialBalance))
      _ <- transactionService.create(Transaction(source, target, amount))
      _ <- transactionService.create(Transaction(target, source, amount))
    } yield (source, target)) { case (s, t) =>
      for {
        sourceBalance <- accountService.get(s).map(_.map(_.balance))
        targetBalance <- accountService.get(t).map(_.map(_.balance))
      } yield {
        sourceBalance.value shouldBe initialBalance
        targetBalance.value shouldBe initialBalance
      }
    }
  }

  "getAll" should "return all transfers" in {
    //when
    val (transactionIds, transactions) = (1 to 10).map { i =>
      (TransactionId(i),
       Transaction(AccountId((i % 6) + 1), AccountId(((i + 1) % 6) + 1), 20.00)) //overwrites ids on db
    }.unzip
    val transactionsFuture = Future.sequence(
      transactions.map(transaction => transactionService.create(transaction)))
    whenReady(transactionsFuture, Timeout(Span(3, Seconds)), Interval(Span(100, Millis))) {
      _ should contain theSameElementsAs transactionIds
    }

    //then
    whenReady(transactionService.getAll().map(_.map(toComparableTuple))) {
      _ should contain theSameElementsAs transactions.map(toComparableTuple)
    }
  }
  "get for specified account" should "return transfer for specified account id" in {
    val transactions = Seq(
      Transaction(AccountId(1), AccountId(2), 20),
      Transaction(AccountId(3), AccountId(4), 20),
      Transaction(AccountId(2), AccountId(1), 10),
      Transaction(AccountId(3), AccountId(4), 10),
    )
    whenReady( for {
      _ <- Future.sequence(transactions.map(transactionService.create))
      transactions <- transactionService.getForAccount(AccountId(1))
    } yield {
      transactions.map(toComparableTuple) should contain theSameElementsAs transactions.map(toComparableTuple)
    }, Timeout(Span(3, Seconds)), Interval(Span(100, Millis)) )(Future.successful) }

}
