package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class Product(id: Option[Long],
                   productName: String,
                   price: BigDecimal ,
                   expDate: LocalDateTime = LocalDateTime.now())

object Product {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[Product] = Json.format[Product]
}


