package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import controllers.order.input.OrderInputDTO
import controllers.order.response.{OrderDetailResponseDTO, OrderResponseDTO}
import domain.dao.{OrderDao, OrderDetailDao}
import domain.models.{Order, OrderDetail}

import java.time.LocalDateTime
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

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
  def listAll(): Future[Iterable[OrderResponseDTO]]

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
  def update(orderInputDTO: OrderInputDTO, id: Long, userId: Long): Future[OrderResponseDTO]

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
      case Some(order) =>
        toOrderResponseDTO(order)
    }
  }
  
  def toOrderResponseDTO ( order: Order ): Future[OrderResponseDTO] = {
    orderDetailDao.findByOrderId(order.id.get).map {
      details => OrderResponseDTO(order.id.get, order.userId, details.map(x => OrderDetailResponseDTO.fromOrderDetail(x)), order.totalPrice, order.orderDate)
    }
  }
  
  def toOrderWithDetails (order: Order) : OrderResponseDTO = {
    val orderDetailList = Await.result(orderDetailDao.findByOrderId(order.id.get), 60.seconds)
    OrderResponseDTO(order.id.get, order.userId, OrderDetailResponseDTO.fromOrderDetailSeq(orderDetailList) , order.totalPrice, order.orderDate)
  }
  
  override def listAll(): Future[Iterable[OrderResponseDTO]] = {
    orderDao.listAll().map(orders => orders.map(order => toOrderWithDetails(order)))
  }
  
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
            OrderResponseDTO(ord.id.getOrElse(-1), userId, orderDetailResponseDTOs, totalPrice, LocalDateTime.now())
          }
        )
      }
    )
  }
  
  override def update(orderInputDTO: OrderInputDTO, id: Long, userId: Long): Future[OrderResponseDTO] = {
    //clear all orderDetails
    orderDetailDao.findByOrderId(id).map(
      details => details.foreach(detail => orderDetailDao.delete(detail.id.get))
    )

    //update new totalPrice
    var totalPrice = BigDecimal(0)
    var orderDetails = orderInputDTO.orderDetails.map(detail =>
    {
      totalPrice = totalPrice.+(detail.price*detail.quantity)
      OrderDetail(None, None, detail.productId, detail.quantity, detail.price)
    })

    //Save order
    val order = Order(Some(id), userId, LocalDateTime.now(), totalPrice)
    orderDao.update(id, userId, order).flatMap(
      ord => {
        orderDetails = orderDetails.map(detail => detail.copy(orderId = Option(ord.id.get)))
        orderDetailDao.saveAll(orderDetails).map(
          details => {
            OrderResponseDTO(ord.id.getOrElse(-1), userId, OrderDetailResponseDTO.fromOrderDetailSeq(details), totalPrice, LocalDateTime.now())
          }
        )
      }
    )
  }

  override def delete(id: Long, userId: Long): Future[Int] = {
    orderDetailDao.findByOrderId(id).map(
      details => details.foreach(detail => orderDetailDao.delete(detail.id.get))
    )
    orderDao.delete(id, userId)
  }
}


