package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Order
import domain.tables.OrderTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[OrderDaoImpl])
trait OrderDao {

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
   * @param Order The Order to save.
   * @return The saved Order.
   */
  def save(order: Order): Future[Order]

  /**
   * Saves all Orders.
   *
   * @param list The Orders to save.
   * @return The saved Orders.
   */
  def saveAll(list: Seq[Order]): Future[Seq[Order]]

  /**
   * Update Order by ID
   * @param id Order's ID
   * @param Order The Order to update
   * @return
   */
  def update(id: Long, userId: Long, order: Order): Future[Order]

  /**
   * Deletes a Order
   * @param email The Order's email to delete.
   * @return The deleted Order.
   */
  def delete(id: Long, userId: Long): Future[Int]
}

/**
 * OrderDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class OrderDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends OrderDao {

  private val orders = TableQuery[OrderTable]

  override def find(id: Long): Future[Option[Order]] = daoRunner.run {
    orders.filter(_.id === id).result.headOption
  }

  override def listAll(): Future[Iterable[Order]] = daoRunner.run {
    orders.result
  }

  override def save(order: Order): Future[Order] = daoRunner.run {
    orders returning orders += order
  }

  override def saveAll(list: Seq[Order]): Future[Seq[Order]] = daoRunner.run {
    (orders ++= list).map(_ => list)
  }

  override def delete(id: Long, userId: Long): Future[Int] = daoRunner.run {
    orders.filter(_.id === id).filter(_.userId === userId).delete
  }

  override def update(id: Long, userId: Long, order: Order): Future[Order] = daoRunner.run {
    orders.filter(_.id === id).filter(_.userId === userId).update(order).map(_ => order)
  }
}
