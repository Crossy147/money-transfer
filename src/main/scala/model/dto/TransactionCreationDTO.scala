package model.dto

import com.avsystem.commons.serialization.HasGenCodec
import model.AccountId

case class TransactionCreationDTO(
    from: AccountId,
    to: AccountId,
    amount: BigDecimal,
)

object TransactionCreationDTO extends HasGenCodec[TransactionCreationDTO]