package service

import dao.TransactionDao
import model.{AccountId, Transaction, TransactionId}

import scala.concurrent.Future

class TransactionService(transactionDao: TransactionDao) {
  def getAll(): Future[Seq[Transaction]] =
    transactionDao.getAll()

  def create(transaction: Transaction): Future[TransactionId] =
    transactionDao.create(transaction)

  def get(transactionId: TransactionId): Future[Option[Transaction]] =
    transactionDao.get(transactionId)

  def getForAccount(accountId: AccountId): Future[Seq[Transaction]] =
    transactionDao.getForAccount(accountId)
}
