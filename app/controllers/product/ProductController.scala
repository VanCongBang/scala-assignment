package controllers.product

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredActionBuilder
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import domain.models.Product
import play.api.Logger
import play.api.data.Form
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import services.ProductService
import utils.auth.{JWTEnvironment, WithRole}
import utils.logging.RequestMarkerContext

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

case class ProductFormInput(productName: String, price: BigDecimal , expDate: LocalDateTime)

/**
 * Takes HTTP requests and produces JSON.
 */
class ProductController @Inject() (cc: ControllerComponents,
                                   productService: ProductService,
                                   silhouette: Silhouette[JWTEnvironment])
                               (implicit ec: ExecutionContext)
  extends AbstractController(cc) with RequestMarkerContext {

  def SecuredAction: SecuredActionBuilder[JWTEnvironment, AnyContent] = silhouette.SecuredAction

  private val logger = Logger(getClass)

  private val form: Form[ProductFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "productName" -> nonEmptyText(maxLength = 128),
        "price" -> bigDecimal,
        "expDate" -> localDateTime
      )(ProductFormInput.apply)(ProductFormInput.unapply)
    )
  }

  def getById(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace(s"getById: $id")
      productService.find(id).map {
        case Some(product) => Ok(Json.toJson(ProductResource.fromProduct(product)))
        case None => NotFound
      }
    }

  def getAll: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace("getAll Products")
      productService.listAll().map { products =>
        Ok(Json.toJson(products.map(product => ProductResource.fromProduct(product))))
      }
    }

  def create: Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace("create Product: ")
      processJsonProduct(None)
    }

  def update(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace(s"update Product id: $id")
      processJsonProduct(Some(id))
    }

  def delete(id: Long): Action[AnyContent] =
    SecuredAction(WithRole[JWTAuthenticator]("Admin", "Operator")).async { implicit request =>
      logger.trace(s"Delete Product: id = $id")
      productService.delete(id).map { deletedCnt =>
        if (deletedCnt == 1) Ok(JsString(s"Delete Product $id successfully"))
        else BadRequest(JsString(s"Unable to delete Product $id"))
      }
    }
  
  private def processJsonProduct[A](id: Option[Long])(implicit request: Request[A]): Future[Result] = {

    def failure(badForm: Form[ProductFormInput]) = {
      Future.successful(BadRequest(JsString("Invalid Input")))
    }

    def success(input: ProductFormInput) = {
      // create a Product from given form input
      val product = Product(id, input.productName, input.price, input.expDate)

      // check case update or create new
      if (id.nonEmpty) {
        productService.updateById(id.get,product).map { product =>
          Ok(Json.toJson(ProductResource.fromProduct(product)))
        }
      }
      else{
        productService.save(product).map { product =>
          Created(Json.toJson(ProductResource.fromProduct(product)))
        }
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
