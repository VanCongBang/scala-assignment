package controllers.order.response

import domain.models.OrderDetail
import play.api.libs.json.{Json, OFormat}

/**
 * DTO for throws response order.
 */
case class OrderDetailResponseDTO(orderId: Long,
                                  productId: Long,
                                  quantity: Long,
                                  price: BigDecimal)

object OrderDetailResponseDTO {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[OrderDetailResponseDTO] = Json.format[OrderDetailResponseDTO]

  def fromOrderDetail(orderDetail: OrderDetail): OrderDetailResponseDTO =
    OrderDetailResponseDTO(orderDetail.orderId.getOrElse(-1), orderDetail.productId, orderDetail.quantity, orderDetail.price )

  def fromOrderDetailSeq (orderDetails : Seq[OrderDetail]) : Seq[OrderDetailResponseDTO] = {
    var orderItemsResponse = Seq[OrderDetailResponseDTO]()
    orderDetails.foreach(detail => orderItemsResponse = orderItemsResponse :+ fromOrderDetail(detail))
    orderItemsResponse
  }
}
