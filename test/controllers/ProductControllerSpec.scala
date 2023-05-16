package controllers

import com.mohiva.play.silhouette.test._
import controllers.product.ProductResource
import domain.models._
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import play.api.libs.json.Json
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers._
import play.api.test._
import utils.auth.JWTEnvironment

import java.time.LocalDateTime
import scala.concurrent.Future

class ProductControllerSpec extends ControllerFixture {

  "ProductController#getById(id: Long)" should {

    "get a product successfully" in {

        // mock response data
        val id = 2L
        val product: Product = Product(Some(id), "Addidas Shoes 1", 10.5, LocalDateTime.now())
//          Some(id), 444L, "Product Title 222", "Product Content 222", LocalDateTime.now(), Some("Product Desc 222"))
        when(mockUserService.retrieve(identity.loginInfo)).thenReturn(Future.successful(Some(identity)))
        when(mockProductService.find(ArgumentMatchers.eq(id))).thenReturn(Future.successful(Some(product)))

        // prepare test request
        val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest(GET, s"/v1/products/${id}")
          .withHeaders(HOST -> "localhost:8080")
          .withAuthenticator[JWTEnvironment](identity.loginInfo)

        // Execute test and then extract result
        val result: Future[Result] = route(app, request).get

        // verify result after test
        status(result) mustEqual OK
        val resProduct: ProductResource = Json.fromJson[ProductResource](contentAsJson(result)).get
        verifyProduct(resProduct, product)
    }
  }

  // Same for remaining methods
  private def verifyProduct(actual: ProductResource, expected: Product): Unit = {
    actual.id mustEqual expected.id.get
    actual.price mustEqual expected.price
    actual.productName mustEqual expected.productName
    actual.expDate mustEqual expected.expDate
  }
}
