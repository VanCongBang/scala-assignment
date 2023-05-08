package controllers.user

import controllers.post.PostResource
import domain.models.{Post, User}
import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

/**
 * The User class
 */
case class UserResource(id: Option[Long], email: String, firstName: String, lastName: String,
                        password: Option[String] = None, role: String, birthDate: LocalDateTime, address: String, phoneNumber: String)

object UserResource {
  /**
   * Mapping to read/write a UserResource out as a JSON value.
   */
  implicit val format: OFormat[UserResource] = Json.format[UserResource]

  def fromUser(user: User): UserResource =
    UserResource(user.id, user.email, user.firstName, user.lastName,
      user.password, user.role, user.birthDate, user.address, user.phoneNumber)
}
