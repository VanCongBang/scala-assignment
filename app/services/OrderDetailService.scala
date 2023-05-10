package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.OrderDetailDao
import domain.models.OrderDetail

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrderDetailServiceImpl])
trait OrderDetailService {
  /**
   * Finds OrderDetails by orderId.
   * @param orderId
   * @return
   */
  def findByOrderId(orderId: Long): Future[Iterable[OrderDetail]]

  /**
   * List all OrderDetails.
   *
   * @return All existing OrderDetails.
   */
  def listAll(): Future[Iterable[OrderDetail]]

  /**
   * Saves a OrderDetail.
   *
   * @param orderDetail The OrderDetail to save.
   * @return The saved OrderDetail.
   */
  def save(orderDetail: OrderDetail): Future[OrderDetail]

  /**
   * Update OrderDetail by ID
   * @param id OrderDetail's ID
   * @param orderDetail The OrderDetail to update
   * @return
   */
  def updateById(id: Long, orderDetail: OrderDetail): Future[OrderDetail]

  /**
   * Delete OrderDetail by ID
   * @param id OrderDetail's ID
   * @return
   */
  def delete(id: Long): Future[Int]
}

/**
 * Handles actions to OrderDetails.
 *
 * @param orderDetailDao The OrderDetail DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class OrderDetailServiceImpl @Inject()(orderDetailDao: OrderDetailDao)(implicit ex: ExecutionContext) extends OrderDetailService {
  override def listAll(): Future[Iterable[OrderDetail]] = orderDetailDao.listAll()

  override def save(orderDetail: OrderDetail): Future[OrderDetail] = orderDetailDao.save(orderDetail)
  
  override def updateById(id: Long, orderDetail: OrderDetail): Future[OrderDetail] = orderDetailDao.updateById(id, orderDetail)

  override def delete(id: Long): Future[Int] = orderDetailDao.delete(id)

  override def findByOrderId(orderId: Long): Future[Iterable[OrderDetail]] = orderDetailDao.findByOrderId(orderId)
}


