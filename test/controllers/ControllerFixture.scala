package controllers

import com.google.inject.AbstractModule
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.test._
import domain.dao._
import domain.models._
import fixtures.TestApplication
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Suite
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import services._
import utils.auth.JWTEnvironment

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global

class ControllerFixture extends PlaySpec with Suite with GuiceOneAppPerSuite with MockitoSugar with ScalaFutures {
  val mockProductService: ProductService = mock[ProductService]
  val mockUserService: UserService = mock[UserService]
  val mockDaoRunner: DaoRunner = mock[DaoRunner]
  val mockUserDao: UserDao = mock[UserDao]
  val mockProductDao: ProductDao = mock[ProductDao]

  val password: String = new BCryptPasswordHasher().hash("fakeP@ssw0rd").password
  val identity: User = User(Some(1L), "bang.admin@nashtechglobal.com", "Admin", "Test" , Some(password), "Admin", LocalDateTime.now(), "Ho Chi Minh City", "098347323")
  implicit val env: Environment[JWTEnvironment] = new FakeEnvironment[JWTEnvironment](Seq(identity.loginInfo -> identity))

  class FakeServiceModule extends AbstractModule with ScalaModule {
    override def configure(): Unit = {
      bind[Environment[JWTEnvironment]].toInstance(env)
      bind[ProductService].toInstance(mockProductService)
      bind[UserService].toInstance(mockUserService)
      bind[DaoRunner].toInstance(mockDaoRunner)
      bind[UserDao].toInstance(mockUserDao)
      bind[ProductDao].toInstance(mockProductDao)
    }
  }

  implicit override lazy val app: Application = TestApplication.appWithOverridesModule(module = new FakeServiceModule())
}
