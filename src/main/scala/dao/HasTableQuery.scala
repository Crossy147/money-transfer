package dao

import slick.lifted.TableQuery

trait HasTableQuery {
  val accounts = TableQuery[AccountScheme]
  val transactions = TableQuery[TransactionScheme]
}
