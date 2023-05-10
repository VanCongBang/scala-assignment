package controllers.order

import domain.models.OrderDetail
import play.api.libs.json.{Json, OFormat}

/**
 * DTO for displaying Order Detail information.
 */
case class OrderDetailResource(id: Long,
                               orderId: Long,
                               productId: Long,
                               quantity: Long,
                               price: BigDecimal)

object OrderDetailResource {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[OrderDetailResource] = Json.format[OrderDetailResource]

  def fromOrderDetail(orderDetail: OrderDetail): OrderDetailResource =
    OrderDetailResource(orderDetail.id.getOrElse(-1), orderDetail.orderId, orderDetail.productId, orderDetail.quantity, orderDetail.price )
}
