package domain.tables

import domain.models.{Order, OrderDetail}
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

class OrderDetailTable(tag: Tag) extends Table[OrderDetail](tag, Some("testing"), "orderdetails") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The order_id FK column */
  def orderId = column[Long]("order_id")
  lazy val orderTable = TableQuery[OrderTable]
  def order = foreignKey("fk_order", orderId, orderTable)(_.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

  /** The product_id FK column */
  def productId = column[Long]("product_id")
  lazy val productTable = TableQuery[ProductTable]
  def product = foreignKey("fk_product", productId, productTable)(_.id.get, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
  
  /** The quantity column */
  def quantity = column[Long]("quantity")

  /** The price column */
  def price = column[BigDecimal]("price")
  
  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * = (id, orderId, productId, quantity, price) <> ((OrderDetail.apply _).tupled, OrderDetail.unapply)

}
