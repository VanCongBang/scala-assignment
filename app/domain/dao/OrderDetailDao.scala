package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.OrderDetail
import domain.tables.OrderDetailTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[OrderDetailDaoImpl])
trait OrderDetailDao {
  /**
   * Finds OrderDetails by orderId.
   * @param orderId
   * @return
   */
  def findByOrderId(orderId: Long): Future[Seq[OrderDetail]]
  /**
   * List all OrderDetails.
   *
   * @return All existing OrderDetails.
   */
  def listAll(): Future[Iterable[OrderDetail]]

  /**
   * Saves a OrderDetail.
   *
   * @param OrderDetail The OrderDetail to save.
   * @return The saved OrderDetail.
   */
  def save(orderDetail: OrderDetail): Future[OrderDetail]

  /**
   * Saves all OrderDetails.
   *
   * @param list The OrderDetails to save.
   * @return The saved OrderDetails.
   */
  def saveAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]]

  /**
   * Update OrderDetail by ID
   * @param id OrderDetail's ID
   * @param OrderDetail The OrderDetail to update
   * @return
   */
  def updateById(id: Long, orderDetail: OrderDetail): Future[OrderDetail]

  /**
   * Deletes a OrderDetail
   * @param email The OrderDetail's email to delete.
   * @return The deleted OrderDetail.
   */
  def delete(id: Long): Future[Int]
}

/**
 * OrderDetailDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class OrderDetailDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends OrderDetailDao {

  private val orderDetails = TableQuery[OrderDetailTable]

  override def listAll(): Future[Iterable[OrderDetail]] = daoRunner.run {
    orderDetails.result
  }

  override def save(orderDetail: OrderDetail): Future[OrderDetail] = daoRunner.run {
    orderDetails returning orderDetails += orderDetail
  }

  override def saveAll(list: Seq[OrderDetail]): Future[Seq[OrderDetail]] = daoRunner.run {
    (orderDetails ++= list).map(_ => list)
  }

  override def delete(id: Long): Future[Int] = daoRunner.run {
    orderDetails.filter(_.id === id).delete
  }

  override def updateById(id: Long, orderDetail: OrderDetail): Future[OrderDetail] = daoRunner.run {
    orderDetails.filter(_.id === id).update(orderDetail).map(_ => orderDetail)
  }

  override def findByOrderId(orderId: Long): Future[Seq[OrderDetail]] = daoRunner.run {
    orderDetails.filter(_.orderId === orderId).result
  }
}
