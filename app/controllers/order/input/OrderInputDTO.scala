package controllers.order.input

import play.api.libs.json.{Json, OFormat}

/**
 * DTO for receive input body request.
 */
case class OrderInputDTO(orderDetails: Seq[OrderDetailInputDTO])

object OrderInputDTO {
  implicit val format: OFormat[OrderInputDTO] = Json.format[OrderInputDTO]
}
