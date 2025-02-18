package domain.tables

import domain.models.User
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

class UserTable(tag: Tag) extends Table[User](tag, Some("testing"), "users") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The email column */
  def email = column[String]("email", O.Unique)

  /** The first_name column */
  def firstName = column[String]("first_name")
  
  /** The last_name column */
  def lastName = column[String]("last_name")

  /** The password column */
  def password = column[Option[String]]("password")

  /** The role column */
  def role = column[String]("role")

  /** The birthDate column */
  def birthDate = column[LocalDateTime]("birth_date")

  /** The address column */
  def address = column[String]("address")

  /** The phoneNumber column */
  def phoneNumber = column[String]("phone_number")
  
  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * = (id, email, firstName, lastName
  , password, role, birthDate, address, phoneNumber) <> ((User.apply _).tupled, User.unapply)
  
}
