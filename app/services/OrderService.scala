package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.order.input.OrderInputDTO
import controllers.order.response.{OrderDetailResponseDTO, OrderResponseDTO}
import domain.dao.{OrderDao, OrderDetailDao}
import domain.models.{Order, OrderDetail}

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {

  /**
   * Finds a Order by id.
   *
   * @param id Order's id.
   * @return The found Order or None if no Order for the given id could be found.
   */
  def find(id: Long): Future[OrderResponseDTO]

  /**
   * List all Orders.
   *
   * @return All existing Orders.
   */
  def listAll(): Future[Iterable[Order]]

  /**
   * Saves a Order.
   *
   * @param order The Order to save.
   * @return The saved Order.
   */
  def save(orderInputDTO: OrderInputDTO, userId: Long): Future[OrderResponseDTO]

  /**
   * Update Order by ID
   * @param id Order's ID
   * @param order The Order to update
   * @return
   */
  def updateById(id: Long, order: Order): Future[Order]

  /**
   * Delete Order by ID
   * @param id Order's ID
   * @return
   */
  def delete(id: Long, userId: Long): Future[Int]
}

/**
 * Handles actions to Orders.
 *
 * @param orderDao The Order DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class OrderServiceImpl @Inject()(orderDao: OrderDao,
                                 orderDetailDao: OrderDetailDao)(implicit ex: ExecutionContext) extends OrderService {
  
  override def find(id: Long): Future[OrderResponseDTO] = {
    orderDao.find(id).flatMap {
      case Some(order) => {
          orderDetailDao.findByOrderId(order.id.get).map {
            details => OrderResponseDTO(id, order.userId, Some(details.map(x => OrderDetailResponseDTO.fromOrderDetail(x))), order.totalPrice, order.orderDate)
//            case Some(details) => OrderResponseDTO(id, order.userId, Some(details.map(x => OrderDetailResponseDTO.fromOrderDetail(x))), order.totalPrice, order.orderDate)
//            case None => OrderResponseDTO(id, order.userId, None, order.totalPrice, order.orderDate)
          }
        }
      }
  }

  override def listAll(): Future[Iterable[Order]] = orderDao.listAll()

  override def save(orderInputDTO: OrderInputDTO, userId: Long): Future[OrderResponseDTO] = {
    //Save order details
    var totalPrice = BigDecimal(0)
    var orderDetails = orderInputDTO.orderDetails.map(detail => 
    {
      totalPrice = totalPrice.+(detail.price*detail.quantity)
      OrderDetail(None, None, detail.productId, detail.quantity, detail.price)
    })
    //Save order
    val order = Order(None, userId, LocalDateTime.now(), totalPrice)
    orderDao.save(order).flatMap(
      ord => {
        orderDetails = orderDetails.map(detail => detail.copy(orderId = Option(ord.id.get)))
        orderDetailDao.saveAll(orderDetails).map(
          details => {
            val orderDetailResponseDTOs = details.map(detail => OrderDetailResponseDTO.fromOrderDetail(detail))
            OrderResponseDTO(ord.id.getOrElse(-1), userId, Some(orderDetailResponseDTOs), totalPrice, LocalDateTime.now())
          }
        )
      }
    )
  }
  
  override def updateById(id: Long, order: Order): Future[Order] = orderDao.updateById(id, order)

  override def delete(id: Long, userId: Long): Future[Int] = orderDao.delete(id, userId)
}


