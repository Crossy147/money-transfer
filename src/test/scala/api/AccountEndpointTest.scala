package api

import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import exceptions.NegativeAccountBalanceException
import model.dto.AccountCreationDTO
import model.{Account, AccountId, Transaction}
import service.{AccountEntities, BaseTest, TransactionEntities}

/**
  * [[BaseTest]] populates account table with entities before each testcase.
  */
class AccountEndpointTest extends EndpointTest {

  val route: Route = accountRoutes.route

  "GET" should "return all accounts" in {
    Get("/accounts") ~> route ~> check {
      responseAs[Seq[Account]] should have length AccountEntities.entities.size
    }
  }

  "GET by id" should "return existing entity" in {
    Get("/accounts/2") ~> route ~> check {
      responseAs[Account] shouldBe AccountEntities.entities.tail.head
    }
  }

  "GET by id" should s"return ${StatusCodes.NotFound.intValue} for nonexistent id" in {
    Get("/accounts/123") ~> route ~> check {
      response.status shouldBe StatusCodes.NotFound
    }
  }

  "GET transactions by id" should "return transactions in which the account was involved" in {
    whenReady(dbInitializer.addTransactions()) { _ =>
      Get("/accounts/1/transactions") ~> route ~> check {
        val expected = Seq(TransactionEntities.entities.head, TransactionEntities.entities.last).map(toComparableTuple)
        responseAs[Seq[Transaction]].map(toComparableTuple) should contain theSameElementsAs expected
      }
    }
  }

  "POST" should "create an entity and return its id" in {
    Post("/accounts", AccountCreationDTO(30)) ~> route ~> check {
      val expectedNextId = AccountEntities.entities.size + 1
      responseAs[AccountId] shouldBe AccountId(expectedNextId)
    }
  }

  it should s"return ${StatusCodes.BadRequest.intValue} for an account with negative balance" in {
    val negativeBalance = -1
    Post("/accounts", AccountCreationDTO(negativeBalance)) ~> route ~> check {
      status shouldBe StatusCodes.BadRequest
      response.entity match {
        case HttpEntity.Strict(_, data)
          if data.utf8String == NegativeAccountBalanceException(negativeBalance).getMessage =>
          succeed
        case _ =>
          fail
      }
    }
  }
}
