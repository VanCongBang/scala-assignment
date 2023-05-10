package services

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.dao.OrderDao
import domain.models.Order

import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[OrderServiceImpl])
trait OrderService {

  /**
   * Finds a Order by id.
   *
   * @param id Order's id.
   * @return The found Order or None if no Order for the given id could be found.
   */
  def find(id: Long): Future[Option[Order]]

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
  def save(order: Order): Future[Order]

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
  def delete(id: Long): Future[Int]
}

/**
 * Handles actions to Orders.
 *
 * @param orderDao The Order DAO implementation.
 * @param ex      The execution context.
 */
@Singleton
class OrderServiceImpl @Inject()(orderDao: OrderDao)(implicit ex: ExecutionContext) extends OrderService {
  override def find(id: Long): Future[Option[Order]] = orderDao.find(id)

  override def listAll(): Future[Iterable[Order]] = orderDao.listAll()

  override def save(order: Order): Future[Order] = orderDao.save(order)
  
  override def updateById(id: Long, order: Order): Future[Order] = orderDao.updateById(id, order)

  override def delete(id: Long): Future[Int] = orderDao.delete(id)
}


