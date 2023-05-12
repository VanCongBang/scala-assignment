package controllers.order.input

import play.api.libs.json.{Json, OFormat}

/**
 * DTO for receive input body request.
 */
case class OrderDetailInputDTO(productId: Long, quantity: Long, price: BigDecimal)

object OrderDetailInputDTO {
  implicit val format: OFormat[OrderDetailInputDTO] = Json.format[OrderDetailInputDTO]
}
