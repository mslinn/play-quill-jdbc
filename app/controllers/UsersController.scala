package controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import models.{User, UserDAO}

class UsersController @Inject() (val controllerComponents: ControllerComponents, userServices: UserDAO) extends BaseController {
  implicit val userWrites: Writes[User] = Json.writes[User]
  implicit val userReads: Reads[User] = (
      Reads.pure(0L) and
      (JsPath \ "name").read[String] and
      (JsPath \ "isActive").read[Boolean]
    )(User.apply _)

  def get(id: Long): Action[AnyContent] = Action { request =>
    userServices.find(id) match {
      case None => NotFound
      case Some(user) => Ok(Json.toJson(user))
    }
  }

  def create: Action[JsValue] = Action(parse.json) { request =>
    Json.fromJson[User](request.body).fold(
      _ => BadRequest,
      user => {
        val userCreated = userServices.create(user)
        Created.withHeaders(LOCATION -> s"/users/${userCreated.id}")
      }
    )
  }

  def delete(id: Long): Action[AnyContent] = Action { request =>
    userServices.find(id) match {
      case None => NotFound
      case Some(user) =>
        userServices.delete(user)
        NoContent
    }
  }

  def update(id: Long): Action[JsValue] = Action(parse.json) { request =>
    Json.fromJson[User](request.body).fold(
      _ => BadRequest,
      user => {
        userServices.update(user.copy(id = id))
        NoContent
      }
    )
  }
}
