package domain.models

import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

case class Order(id: Option[Long],
                 userId : Long,
                 orderDate: LocalDateTime = LocalDateTime.now(),
                 totalPrice: BigDecimal)

object Order {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[Order] = Json.format[Order]
}


