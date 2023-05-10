package controllers.order

import domain.models.{Order, OrderDetail}
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime
import java.util.Optional

/**
 * DTO for displaying Order information.
 */
case class OrderResource(id: Long,
                         userId: Long,
//                         orderDetail: Optional[OrderDetailResource],
                         totalPrice: BigDecimal,
                         orderDate: LocalDateTime)

object OrderResource {
  /**
   * Mapping to read/write a OrderResource out as a JSON value.
   */
  implicit val format: OFormat[OrderResource] = Json.format[OrderResource]

//  def fromOrderWithDetails(order: Order, orderDetails: List[OrderDetail]): OrderResource = {
//    OrderResource(order.id.getOrElse(-1), order.userId, orderDetails.map(x => OrderDetailResource.fromOrderDetail(x)), order.totalPrice, order.orderDate)
//  }

  def fromOrder(order: Order): OrderResource = {
    OrderResource(order.id.getOrElse(-1), order.userId, order.totalPrice, order.orderDate)
  }
}
