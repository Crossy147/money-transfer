package api
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Route
import exceptions.{AccountException, PrerequisiteException, TransactionException}
import model.dto.TransactionCreationDTO
import model.{AccountId, Transaction, TransactionId}
import org.scalatest.Assertion
import service.{BaseTest, TransactionEntities}

/**
  * [[BaseTest]] populates account table with entities before each testcase.
  */
class TransactionEndpointTest extends EndpointTest {

  override val route: Route = transactionRoutes.route

  "POST" should "create new transaction and return its id" in {
    Post("/transactions", TransactionCreationDTO(AccountId(1), AccountId(2), 20)) ~> route ~> check {
      responseAs[TransactionId] shouldBe TransactionId(1)
      response.status shouldBe Created
    }
  }

  it should "reject a transaction with non-positive amount" in {
    val amount = -10
    rejectsWithExceptionMsg(1, 2, amount)(PrerequisiteException.NonPositiveAmountException(amount))
  }

  it should "not accept transaction with the same source and target" in {
    val source = 1
    rejectsWithExceptionMsg(source, 1, 10.0)(PrerequisiteException.SameSourceAndTargetAccountException(AccountId(source)))
  }

  it should "not accept transaction for incorrect source account" in {
    val source = 10
    rejectsWithExceptionMsg(source, 1, 20.0)(AccountException.NonExistentSourceAccountException(AccountId(source)))
  }

  it should "not accept a transaction for incorrect target account" in {
    val target = 20
    rejectsWithExceptionMsg(1, target, 10.0)(AccountException.NonExistentTargetAccountException(AccountId(target)))
  }

  it should "not accept a transaction for incorrect  source and target accounts" in {
    val source = 21
    val target = 20
    rejectsWithExceptionMsg(source, target, 10.0)(AccountException.NonExistentSourceAndTargetAccountException(AccountId(source), AccountId(target)))
  }

  it should "not accept a transaction when source account does not have enough founds" in {
    val source = 1
    val amount = 10000.0
    rejectsWithExceptionMsg(source, 2, amount)(AccountException.NotEnoughFundsException(AccountId(source), amount))
  }

  "GET" should "return all transactions" in {
    whenReady(dbInitializer.addTransactions()) { _ =>
      Get("/transactions") ~> route ~> check {
        responseAs[Seq[Transaction]].map(transaction => (transaction.from, transaction.to, transaction.amount)) should contain theSameElementsAs
          TransactionEntities.entities.map(tr => (tr.from, tr.to, tr.amount))
        response.status shouldBe OK
      }
    }
  }

  def rejectsWithExceptionMsg(source: Long, target: Long, amount: BigDecimal)(exception: TransactionException): Assertion = {
    Post("/transactions", TransactionCreationDTO(AccountId(source), AccountId(target), amount)) ~> route ~> check {
      status shouldBe BadRequest
      response.entity match {
        case HttpEntity.Strict(_, data) if data.utf8String == exception.getMessage => succeed
        case _ => fail("Incorrect response")
      }
    }
  }
}
