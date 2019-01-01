package service

import context.ApplicationContextForTests
import model.{AccountId, Transaction}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Await
import scala.concurrent.duration._

trait BaseTest
    extends AsyncFlatSpec
    with ApplicationContextForTests
    with Matchers
    with BeforeAndAfterEach
    with ScalaFutures
    with OptionValues {

  def toComparableTuple(transaction: Transaction): (AccountId, AccountId, BigDecimal) =
    (transaction.from, transaction.to, transaction.amount)


  override protected def beforeEach(): Unit =
    Await.result(dbInitializer.initializeWithSampleAccounts(), 5.seconds)


  override protected def afterEach(): Unit =
    Await.result(dbInitializer.clean(), 5.seconds)

}
