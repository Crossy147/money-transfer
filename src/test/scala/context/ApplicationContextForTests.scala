package context

import com.softwaremill.macwire._
import service.DbInitializer

trait ApplicationContextForTests extends ApplicationContext {
  lazy val dbInitializer: DbInitializer = wire[DbInitializer]
}
