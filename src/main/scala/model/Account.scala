package model

import com.avsystem.commons.serialization.{HasGenCodec, transparent}
import model.dto.AccountCreationDTO
import slick.lifted.MappedTo

import scala.math.BigDecimal.RoundingMode

@transparent final case class AccountId(value: Long) extends AnyVal with MappedTo[Long]
object AccountId extends HasGenCodec[AccountId]

case class Account(balance: BigDecimal, id: Option[AccountId] = None) {
  balance.setScale(2, RoundingMode.HALF_UP)
}

object Account extends HasGenCodec[Account] {
  def fromDTO(dto: AccountCreationDTO): Account = Account(dto.initialBalance)
}
