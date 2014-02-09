package models.frontend

import play.api.libs.json.{Json, JsValue, Writes}
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import models.{Account, UsState}
import db.UsStateDto

class FrontendAccount {
  @JsonProperty
  @JsonDeserialize(contentAs = classOf[java.lang.Long])
  var id: Option[Long] = None

  @JsonProperty
  var firstName: String = _

  @JsonProperty
  var lastName: String = _

  @JsonProperty
  var emailAddress: String = _

  @JsonProperty
  var usState: UsState = _

  def this(account: Account) = {
    this()

    this.id = account.id
    this.firstName = account.firstName
    this.lastName = account.lastName
    this.emailAddress = account.emailAddress
    this.usState = UsStateDto.getOfId(account.usStateId)
  }
}

object FrontendAccount {
  implicit val writes = new Writes[FrontendAccount] {
    def writes(account: FrontendAccount): JsValue = {
      Json.obj(
        "id" -> account.id,
        "firstName" -> account.firstName,
        "lastName" -> account.lastName,
        "emailAddress" -> account.emailAddress,
        "usState" -> account.usState
      )
    }
  }
}