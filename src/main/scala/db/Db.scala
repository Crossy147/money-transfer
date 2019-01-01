package db

import com.typesafe.config.ConfigFactory
import dao.HasTableQuery
import slick.basic.DatabaseConfig
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._


final class Db(configPath: String) extends HasTableQuery {
  val instance = {
    val config = ConfigFactory.load()
    val databaseConfig: DatabaseConfig[H2Profile.type] = DatabaseConfig.forConfig[H2Profile.type](configPath, config)
    databaseConfig.db
  }
  locally {
    instance.createSession()
    instance.run(DBIO.seq((accounts.schema ++ transactions.schema).create))
  }
}
