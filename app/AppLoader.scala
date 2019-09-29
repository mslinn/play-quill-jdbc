import java.io.Closeable
import controllers.UsersController
import io.getquill._
import javax.sql.DataSource
import models.UserDAO
import play.api.ApplicationLoader.Context
import play.api._
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.{DBComponents, HikariCPComponents}
import play.api.inject.{Injector, NewInstanceInjector, SimpleInjector}
import play.api.routing.Router
import play.api.routing.sird._
import play.filters.HttpFiltersComponents

class AppLoader extends ApplicationLoader {
  override def load(context: Context): Application =
    new BuiltInComponentsFromContext(context)
      with DBComponents
      with EvolutionsComponents
      with HikariCPComponents
      with HttpFiltersComponents {

    private lazy val dataSource = dbApi.database("default").dataSource.asInstanceOf[DataSource with Closeable]
    implicit lazy val ctx: H2JdbcContext[SnakeCase] = new H2JdbcContext(SnakeCase, dataSource) // new DBContext("db.default")

    lazy val users = new UserDAO(ctx)
    lazy val usersController = new UsersController(users)

    val router: Router = Router.from {
      case GET(p"/users/${ long(id) }")    => usersController.get(id)
      case POST(p"/users")                 => usersController.create
      case DELETE(p"/users/${ long(id) }") => usersController.delete(id)
      case PUT(p"/users/${ long(id) }")    => usersController.update(id)
    }

    override lazy val injector: Injector =
      new SimpleInjector(NewInstanceInjector) + users + router + cookieSigner + csrfTokenSigner + httpConfiguration + tempFileCreator /*+ global*/

    // play.api.db.evolutions.Evolutions.applyEvolutions(dbApi.database("default"))
    applicationEvolutions // See https://www.playframework.com/documentation/2.7.x/Evolutions
  }.application
}
