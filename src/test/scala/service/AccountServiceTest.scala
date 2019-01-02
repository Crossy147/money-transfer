package service

import exceptions.NegativeAccountBalanceException
import model.{Account, AccountId}

/**
  * [[BaseTest]] populates account table with entities before each testcase.
  */
class AccountServiceTest extends BaseTest {

  it should "create an account with non negative balance and force unified precision" in {
    val initial = Account(100.123, None)
    val expectedValueWithPrecision: BigDecimal = 100.12
    whenReady(accountService.create(initial)) { id =>
      whenReady(accountService.get(id)) { account =>
        account.map(_.balance).value shouldBe expectedValueWithPrecision
      }
    }
  }

  it should "not create an account with negative balance" in {
    val negativeBalance = -10.00
    whenReady(accountService.create(Account(negativeBalance, None)).failed) { ex =>
      ex shouldBe NegativeAccountBalanceException(negativeBalance)
      whenReady(accountService.getAll()) { res =>
        res.size shouldBe AccountEntities.entities.size
      }
    }
  }

  it should "get all accounts" in {
    whenReady(accountService.getAll()) {
      accounts => accounts.map(_.balance) should contain theSameElementsAs
        AccountEntities.entities.map(_.balance)
    }
  }

  it should "get account if exists" in {
    whenReady(accountService.get(AccountId(2))) { account =>
      account.flatMap(_.id).value shouldBe AccountId(2)
    }
  }

  it should "get none if account does not exist" in {
    whenReady(accountService.get(AccountId(20))) {
      _ shouldBe None
    }
  }

}
