package models

import db.DbContext

case class User (id: Long, name: String, isActive: Boolean)

class UserDAO(val db: DbContext) {
  import db._

  val users = quote(querySchema[User]("users"))

  def find(id: Long): Option[User] = run(users.filter(c => c.id == lift(id) && c.isActive)).headOption

  def create(user: User): User = user.copy(id = run(users.insert(lift(user)).returningGenerated(_.id)))

  def delete(user: User): Long = run(users.filter(_.id == lift(user.id)).delete)

  def update(user: User): Long = run(users.filter(_.id == lift(user.id)).update(lift(user)))

}
