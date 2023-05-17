package domain.dao

import com.google.inject.{ImplementedBy, Inject, Singleton}
import domain.models.Product
import domain.tables.ProductTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

@ImplementedBy(classOf[ProductDaoImpl])
trait ProductDao {

  /**
   * Finds a Product by id.
   *
   * @param id Product's id.
   * @return The found Product or None if no Product for the given id could be found.
   */
  def find(id: Long): Future[Option[Product]]
  
  /**
   * List all Products.
   *
   * @return All existing Products.
   */
  def listAll(): Future[Iterable[Product]]

  /**
   * Saves a Product.
   *
   * @param Product The Product to save.
   * @return The saved Product.
   */
  def save(product: Product): Future[Product]

  /**
   * Saves all Products.
   *
   * @param list The Products to save.
   * @return The saved Products.
   */
  def saveAll(list: Seq[Product]): Future[Seq[Product]]

  /**
   * Update Product by ID
   * @param id Product's ID
   * @param Product The Product to update
   * @return
   */
  def update(product: Product): Future[Product]

  /**
   * Deletes a Product
   * @param email The Product's email to delete.
   * @return The deleted Product.
   */
  def delete(id: Long): Future[Int]
}

/**
 * ProductDao implementation class
 *
 * @param daoRunner DaoRunner for running query in a transaction
 * @param ec Execution context
 */
@Singleton
class ProductDaoImpl @Inject()(daoRunner: DaoRunner)(implicit ec: DbExecutionContext)
  extends ProductDao {

  private val products = TableQuery[ProductTable]

  override def find(id: Long): Future[Option[Product]] = daoRunner.run {
    products.filter(_.id === id).result.headOption
  }

  override def listAll(): Future[Iterable[Product]] = daoRunner.run {
    products.result
  }

  override def save(product: Product): Future[Product] = daoRunner.run {
    products returning products += product
  }

  override def saveAll(list: Seq[Product]): Future[Seq[Product]] = daoRunner.run {
    (products ++= list).map(_ => list)
  }

  override def delete(id: Long): Future[Int] = daoRunner.run {
    products.filter(_.id === id).delete
  }

  override def update(product: Product): Future[Product] = daoRunner.run {
    products.filter(_.id === product.id).update(product).map(_ => product)
  }
}
