package api

import akka.http.scaladsl.model.{HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import exceptions.NegativeAccountBalanceException
import model.dto.AccountCreationDTO
import model.{Account, AccountId}
import service.{AccountEntities, BaseTest}

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

  "POST" should "create an entity and return its id" in {
    Post("/accounts", AccountCreationDTO(30)) ~> route ~> check {
      responseAs[AccountId] shouldBe AccountId(AccountEntities.entities.size + 1)
    }
  }

  it should "return internal error for an account with negative balance" in {
    val negativeBalance = -1
    Post("/accounts", AccountCreationDTO(negativeBalance)) ~> route ~> check {
      status shouldBe StatusCodes.InternalServerError
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
