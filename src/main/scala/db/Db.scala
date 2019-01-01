package db

import com.typesafe.config.ConfigFactory
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile

final class Db(configPath: String) {
  val instance = {
    val config = ConfigFactory.load()
    val databaseConfig: DatabaseConfig[H2Profile.type] = DatabaseConfig.forConfig[H2Profile.type](configPath, config)
    databaseConfig.db
  }
  locally {
    instance.createSession()
  }
}
