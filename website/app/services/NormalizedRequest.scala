package services

import play.api.mvc.RequestHeader

class NormalizedRequest(request: RequestHeader) extends RequestHeader {

  val queryString = request.queryString
  val remoteAddress = request.remoteAddress
  val method = request.method
  val headers = request.headers
  val version = request.version
  val tags = request.tags
  val id = request.id

  val path = pathStrippedFromTrailingSlash()
  val uri = uriStrippedFromTrailingSlash()

  private def pathStrippedFromTrailingSlash(): String = {
    if (request.path == "/")
      request.path
    else
      request.path.stripSuffix("/")
  }

  private def uriStrippedFromTrailingSlash(): String = {
    if (request.queryString.isEmpty) {
      path
    } else {
      path + "?" + request.rawQueryString
    }
  }
}

object NormalizedRequest {
  def apply(request: RequestHeader) = new NormalizedRequest(request)
}