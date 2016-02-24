package controllers

import config.FrontendAuthConnector
import play.api.mvc.Action
import uk.gov.hmrc.play.config.RunMode
import uk.gov.hmrc.play.frontend.auth.{Actions, AuthContext}
import uk.gov.hmrc.play.frontend.controller.FrontendController


object HomeController extends HomeController {
  val authConnector = FrontendAuthConnector
}

trait HomeController extends FrontendController with Actions with RunMode {

  def welcome() = Action { implicit request =>
    Ok(views.html.welcome(request))
  }
}
