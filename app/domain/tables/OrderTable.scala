package domain.tables

import slick.jdbc.PostgresProfile.api._
import java.time.LocalDateTime
import domain.models.Order

class OrderTable(tag: Tag) extends Table[Order](tag, Some("testing"), "orders") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The user_id FK column */
  def userId = column[Long]("user_id")
  
  /** The total_price column */
  def totalPrice = column[BigDecimal]("total_price")
  
  /** The order_date column */
  def orderDate = column[LocalDateTime]("order_date")
  
  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * = (id, userId, orderDate, totalPrice) <> ((Order.apply _).tupled, Order.unapply)

  val users = TableQuery[UserTable]
  def user = foreignKey("fk_user", userId, users)(_.id.get)
}
