package models

import play.api.libs.json.{Json, JsValue, Writes}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

case class Account(@JsonDeserialize(contentAs = classOf[java.lang.Long])
                id: Option[Long] = None,

                firstName: String,
                lastName: String,
                emailAddress: String,
                password: Option[String],
                usStateId: Long,
                creationTimestamp: Option[Int])
