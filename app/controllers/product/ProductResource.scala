package controllers.product

import play.api.libs.json.{Json, OFormat}
import domain.models.Product
import java.time.LocalDateTime

/**
 * DTO for displaying post information.
 */
case class ProductResource(id: Long,
                           productName: String,
                           price: BigDecimal ,
                           expDate: LocalDateTime)

object ProductResource {
  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[ProductResource] = Json.format[ProductResource]

  def fromProduct(product: Product): ProductResource =
    ProductResource(product.id.getOrElse(-1), product.productName, product.price, product.expDate)
}
