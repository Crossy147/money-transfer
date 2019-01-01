package utils

import com.typesafe.config.ConfigFactory

trait HttpConfig {
  private lazy val config = ConfigFactory.load()
  lazy val httpHost = config.getString("http.host")
  lazy val httpPort = config.getInt("http.port")
}
