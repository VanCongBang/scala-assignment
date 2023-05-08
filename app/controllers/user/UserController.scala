package controllers.user

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.User
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.UserService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class UserFormInput(email: String, firstName: String, lastName: String,
                         password: Option[String] = None, role: String, birthDate: LocalDateTime, address: String, phoneNumber: String)

/**
 * Takes HTTP requests and produces JSON.
 */
class UserController @Inject() (cc: ControllerComponents,
                                userService: UserService,
                                silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[UserFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "email" -> nonEmptyText(maxLength = 128),
        "firstName" -> nonEmptyText(maxLength = 128),
        "lastName" -> nonEmptyText(maxLength = 128),
        "password" -> optional(text),
        "role" -> nonEmptyText(maxLength = 128),
        "birthDate" -> localDateTime,
        "address" -> nonEmptyText(maxLength = 128),
        "phoneNumber" -> nonEmptyText(maxLength = 128)
      )(UserFormInput.apply)(UserFormInput.unapply)
    )
  }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"getById: $id")
      userService.find(id).map {
        case Some(user) => Ok(Json.toJson(UserResource.fromUser(user)))
        case None => NotFound
      }
    }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace("getAll Users")
      userService.listAll().map { users =>
        Ok(Json.toJson(users.map(user => UserResource.fromUser(user))))
      }
    }

  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace("create User: ")
      processJsonUser(None)
    }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"update User id: $id")
      processJsonUser(Some(id))
    }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin")).async { implicit request =>
      logger.trace(s"Delete user: id = $id")
      userService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete user $id successfully"))
        else BadRequest(JsString(s"Unable to delete user $id"))
      }
    }
  
  private def processJsonUser[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[UserFormInput]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: UserFormInput) = {
      // create a user from given form input
      val user = User(id, input.email, input.firstName, input.lastName,
        input.password, input.role, input.birthDate, input.address, input.phoneNumber)

      if (id.nonEmpty) {
        userService.updateById(id.get,user).map { user =>
          Ok(Json.toJson(UserResource.fromUser(user)))
        }
      }
      else{
        userService.save(user).map { user =>
          Created(Json.toJson(UserResource.fromUser(user)))
        }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
