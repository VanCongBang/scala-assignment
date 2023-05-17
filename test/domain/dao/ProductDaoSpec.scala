package domain.dao

import domain.models.Product
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDateTime

class ProductDaoSpec extends AbstractDaoTest with MockitoSugar with ScalaFutures {

  val productDao: ProductDao = get[ProductDao]

  // insert some products for testing, the Product's id will be generated by DB engine with auto increment
  val product1: Product = Product(None, "Product Title 111", 20, LocalDateTime.now())
  val product2: Product = Product(None, "Product Title 112", 30, LocalDateTime.now())
  val product3: Product = Product(None, "Product Title 113", 40, LocalDateTime.now())
  val product4: Product = Product(None, "Product Title 114", 50, LocalDateTime.now())

  override protected def beforeAll(): Unit = {

    // Save prepared data to db
    productDao.save(product1).futureValue // id = 1
    productDao.save(product2).futureValue // id = 2
    productDao.save(product3).futureValue // id = 3
    productDao.save(product4).futureValue // id = 4
  }

  "ProductDao#find(id: Long)" should {

    "get a product successfully" in {
      val result = productDao.find(1L).futureValue
      result.isEmpty mustBe false
      val product = result.get
      product.id.get mustEqual 1L
      product.productName mustEqual product1.productName
      product.price mustEqual product1.price
      product.expDate mustEqual product1.expDate
    }

    "product not found" in {
      val result = productDao.find(5L).futureValue
      result.isEmpty mustBe true
    }
  }

  "ProductDao#listAll" should {

    "get all products successfully" in {
      val result = productDao.listAll().futureValue
      result.size mustBe 4
      result.map(_.id.get) must contain allOf(1L, 2L, 3L, 4L)
    }
  }

  "ProductDao#save(product)" should {

    "save a product successfully" in {
      val product5 = Product(None, "Product Title 555", 50, LocalDateTime.now()) 
      productDao.save(product5).futureValue

      val result = productDao.find(5L).futureValue
      result.isEmpty mustBe false
      val product = result.get
      product.id.get mustEqual 1L
      product.productName mustEqual product1.productName
      product.price mustEqual product1.price
      product.expDate mustEqual product1.expDate
    }
  }

  "ProductDao#update(product)" should {

    "update a product successfully" in {
      val product2 = Product(Some(2L), "Product Title 222", 20, LocalDateTime.now())
      productDao.update(product2).futureValue

      val result = productDao.find(2L).futureValue
      result.isEmpty mustBe false
      val product = result.get
      product.id.get mustEqual product2.id.get
      product.productName mustEqual product2.productName
      product.price mustEqual product2.price
      product.expDate mustEqual product2.expDate
    }
  }

  "ProductDao#delete(id: Long)" should {

    "delete a product successfully" in {
      productDao.delete(3L).futureValue

      val result = productDao.find(3L).futureValue
      result.isEmpty mustBe true // product is no longer exists.

      val resultAll = productDao.listAll().futureValue
      resultAll.size mustBe 4
      resultAll.map(_.id.get) must contain allOf(1L, 2L, 4L, 5L) // product 5 is inserted in the above test
    }
  }
}
