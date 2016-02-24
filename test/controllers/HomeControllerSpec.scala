package controllers

import java.util.UUID

import builders.AuthBuilder
import org.jsoup.Jsoup
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import builders.SessionBuilder
import scala.concurrent.Future


class HomeControllerSpec extends PlaySpec with OneServerPerSuite with MockitoSugar with BeforeAndAfterEach {

  val mockAuthConnector = mock[AuthConnector]

  object TestHomeController extends HomeController {
    val authConnector = mockAuthConnector
  }

  override def beforeEach(): Unit = {
    reset(mockAuthConnector)
  }

  "HomeController" must {

    "welcome" must {
      "unauthorised users" must {
        "load the welcome page" in {
          welcomeWithUnAuthorisedUser { result =>
            status(result) must equal(OK)
            val document = Jsoup.parse(contentAsString(result))
            document.title() must be("Saml Title")
            document.getElementById("welcome-header").text() must be("Saml Header")
            document.getElementById("welcome-text").text() must include("text")
           }
        }
      }

      "Unauthenticated users" must {
        "load the welcome page" in {
          welcomeWithUnAuthenticated { result =>
            status(result) must equal(OK)
            val document = Jsoup.parse(contentAsString(result))
            document.title() must be("Saml Title")

          }
        }
      }

      "Authenticated users" must {
        "load the welcome page" in {
          welcomeWithAuthorisedUser { result =>
            status(result) must equal(OK)
            val document = Jsoup.parse(contentAsString(result))
            document.title() must be("Saml Title")
          }
        }
      }
    }

  }

  def welcomeWithUnAuthorisedUser(test: Future[Result] => Any) {
    val userId = s"user-${UUID.randomUUID}"
    AuthBuilder.mockUnAuthorisedUser(userId, mockAuthConnector)
    val result = TestHomeController.welcome.apply(SessionBuilder.buildRequestWithSession(userId))
    test(result)
  }

  def welcomeWithUnAuthenticated(test: Future[Result] => Any) {
    val result = TestHomeController.welcome.apply(SessionBuilder.buildRequestWithSessionNoUser())
    test(result)
  }

  def welcomeWithAuthorisedUser(test: Future[Result] => Any) {
    val userId = s"user-${UUID.randomUUID}"
    AuthBuilder.mockAuthorisedAgent(userId, mockAuthConnector)
    val result = TestHomeController.welcome.apply(SessionBuilder.buildRequestWithSession(userId))
    test(result)
  }

}
