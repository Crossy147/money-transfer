package model

import com.avsystem.commons.misc.Timestamp
import com.avsystem.commons.serialization.{HasGenCodec, transparent}
import model.dto.TransactionCreationDTO
import slick.lifted.MappedTo

import scala.math.BigDecimal.RoundingMode

@transparent final case class TransactionId(value: Long) extends AnyVal with MappedTo[Long]
object TransactionId extends HasGenCodec[TransactionId]

case class Transaction(from: AccountId,
                       to: AccountId,
                       amount: BigDecimal,
                       timestamp: Timestamp = Timestamp.now(),
                       id: Option[TransactionId] = None)

object Transaction extends HasGenCodec[Transaction] {
  def fromDto(dto: TransactionCreationDTO): Transaction =
    Transaction(dto.from, dto.to, dto.amount)
}
