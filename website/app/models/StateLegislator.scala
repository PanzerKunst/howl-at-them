package models

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

case class StateLegislator(@JsonDeserialize(contentAs = classOf[java.lang.Long])
                           id: Option[Long] = None,

                           firstName: String,
                           lastName: String,
                           titleId: Long,
                           politicalPartyId: Long,
                           usStateId: Long,
                           district: Int,
                           phoneNumber: Option[String],
                           ideologyRanking: Option[Float],
                           creationTimestamp: Option[Int])
