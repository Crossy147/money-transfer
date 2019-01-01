package model.dto

import com.avsystem.commons.serialization.HasGenCodec

final case class AccountCreationDTO(initialBalance: BigDecimal)
object AccountCreationDTO extends HasGenCodec[AccountCreationDTO]