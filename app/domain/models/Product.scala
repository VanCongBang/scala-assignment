package domain.models

import java.time.LocalDateTime

case class Product(id: Option[Long],
                   productName: String,
                   price: BigDecimal ,
                   expDate: LocalDateTime = LocalDateTime.now())




