package models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

case class Account(@JsonDeserialize(contentAs = classOf[java.lang.Long])
                   id: Option[Long] = None,

                   username: String,
                   password: Option[String] = None)
