package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import controllers.order.input.{OrderDetailInputDTO, OrderInputDTO}
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import play.api.{Configuration, Logger}
import services.OrderService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Takes HTTP requests and produces JSON.
 */
class OrderController @Inject() (cc: ControllerComponents,
                                   orderService: OrderService,
                                   silhouette: Silhouette[JWTEnvironment],
                                   configuration: Configuration)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[OrderInputDTO] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "orderDetails" -> seq(
            mapping(
            "productId" -> longNumber,
            "quantity" -> longNumber,
            "price" -> bigDecimal
            )(OrderDetailInputDTO.apply)(OrderDetailInputDTO.unapply)
        ))(OrderInputDTO.apply)(OrderInputDTO.unapply)
    )
  }
  
  def getById(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace(s"getById: $id")
          orderService.find(id).map { orders =>
        Ok(Json.toJson(orders))
      }
    }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace("getAll Orders")
      orderService.listAll().map { orders =>
        Ok(Json.toJson(orders))
      }
    }
  
  def create: Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace("create Order: ")
      processJsonOrder(None, request.identity.id.get)
  }

  def update(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace(s"update Order id: $id")
      processJsonOrder(Some(id), request.identity.id.get)
    }

  def delete(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.user"))).async { implicit request =>
      logger.trace(s"Delete Order: id = $id")
      orderService.delete(id, request.identity.id.get).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete Order $id successfully"))
        else BadRequest(JsString(s"Unable to delete Order $id"))
      }
    }
  
  private def processJsonOrder[A](id: Option[Long], userId: Long)(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[OrderInputDTO]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: OrderInputDTO) = {
      if (id.nonEmpty){
        orderService.update(input, id.get, userId).map {
          order => Ok(Json.toJson(order))
        }
      }
      else{
        orderService.save(input, userId).map {
          order => Created(Json.toJson(order))
        }
      }
    }
    
    form.bindFromRequest().fold(failure, success)
  }
}
