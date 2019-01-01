package api

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import rest.routes.GenCodecMarshalling
import service.BaseTest

import scala.concurrent.ExecutionContext

abstract class EndpointTest
    extends BaseTest
    with GenCodecMarshalling
    with ScalatestRouteTest {

  implicit val exc = ExecutionContext.global
  val route: Route
}
