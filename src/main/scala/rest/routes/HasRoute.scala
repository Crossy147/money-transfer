package rest.routes

import akka.http.scaladsl.server.Route

trait HasRoute {
  val route: Route
}
