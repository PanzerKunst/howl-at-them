import concurrent.Future
import play.api.mvc.{RequestHeader, Handler}
import play.api.GlobalSettings
import services.NormalizedRequest
import play.api.mvc.Results._

object Global extends GlobalSettings {
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    super.onRouteRequest(NormalizedRequest(request))
  }

  override def onHandlerNotFound(request: RequestHeader) = {
    Future.successful(NotFound(
      views.html.notFound()
    ))
  }
}