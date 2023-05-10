package controllers.order

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import controllers.post.PostFormInput
import domain.models.Order
import play.api.data.Form
import play.api.libs.json.{JsString, Json, OFormat}
import play.api.mvc._
import play.api.{Configuration, Logger}
import services.{OrderDetailService, OrderService, UserService}
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class OrderFormInput(orderDetails: Seq[OrderDetailInput])
object OrderFormInput {
  implicit val format: OFormat[OrderFormInput] = Json.format[OrderFormInput]
}

case class OrderDetailInput(productId: Long, quantity: Long, price: BigDecimal)
object OrderDetailInput {
  implicit val format: OFormat[OrderDetailInput] = Json.format[OrderDetailInput]
}

/**
 * Takes HTTP requests and produces JSON.
 */
class OrderController @Inject() (cc: ControllerComponents,
                                   orderService: OrderService,
                                  orderDetailService: OrderDetailService,
                                   silhouette: Silhouette[JWTEnvironment],
                                   configuration: Configuration)
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[OrderFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "orderDetails" -> seq(
            mapping(
            "productId" -> longNumber,
            "quantity" -> longNumber,
            "price" -> bigDecimal
            )(OrderDetailInput.apply)(OrderDetailInput.unapply)
        ))(OrderFormInput.apply)(OrderFormInput.unapply)
    )
  }
  
  def getById(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace(s"getById: $id")
      val orderDetails = orderDetailService.findByOrderId(id)
      orderService.find(id).map {
        case Some(order) => Ok(Json.toJson(OrderResource.fromOrder(order)))
        case None => NotFound
      }
    }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace("getAll Orders")
      orderService.listAll().map { orders =>
        Ok(Json.toJson(orders.map(order => OrderResource.fromOrder(order))))
      }
    }
  
  def create: Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace("create Order: ")
      processJsonOrder(None, request.id)
  }

  def update(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace(s"update Order id: $id")
      processJsonOrder(Some(id), request.id)
    }

  def delete(id: Long): Action[AnyContent] =
        SecuredAction(WithRole[JWTAuthenticator](configuration.underlying.getString("role.admin"), configuration.underlying.getString("role.op"))).async { implicit request =>
      logger.trace(s"Delete Order: id = $id")
      orderService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete Order $id successfully"))
        else BadRequest(JsString(s"Unable to delete Order $id"))
      }
    }
  
  private def processJsonOrder[A](id: Option[Long], userId: Long)(implicit request: Request[A]): Future[Result] = {
    def failure(badForm: Form[OrderFormInput]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: OrderFormInput) = {
      //calculated totalPrice
//      val totalPrice : BigDecimal = input.orderDetails.foreach{
//        detail => totalPrice.+(detail.price * detail.quantity)
//      }

      val order = Order(id, userId, LocalDateTime.now(), userId)
      // check case update or create new
      if (id.nonEmpty) {
        orderService.updateById(id.get,order).map { order =>
          Ok(Json.toJson(OrderResource.fromOrder(order)))
        }
      }
      else{
        orderService.save(order).map { order =>
          Created(Json.toJson(OrderResource.fromOrder(order)))
        }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
