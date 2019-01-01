package service

import dao.AccountDao
import model.{Account, AccountId}

import scala.concurrent.Future

class AccountService(accountDao: AccountDao) {
  def getAll(): Future[Seq[Account]] = accountDao.getAll
  def create(account: Account): Future[AccountId] = accountDao.create(account)
  def get(accountId: AccountId): Future[Option[Account]] =  accountDao.getById(accountId)
}
