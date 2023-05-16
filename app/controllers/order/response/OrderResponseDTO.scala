package controllers.order.response

import controllers.order.input.OrderInputDTO
import domain.models.{Order, OrderDetail}
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

/**
 * DTO for throws response order.
 */
case class OrderResponseDTO(id: Long,
                            userId: Long,
                            orderDetails: Seq[OrderDetailResponseDTO],
                            totalPrice: BigDecimal,
                            orderDate: LocalDateTime)

object OrderResponseDTO {
  /**
   * Mapping to read/write a OrderResource out as a JSON value.
   */
  implicit val format: OFormat[OrderResponseDTO] = Json.format[OrderResponseDTO]
}
