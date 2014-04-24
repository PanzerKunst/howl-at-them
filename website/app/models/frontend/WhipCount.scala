package models.frontend

import models.SupportLevel

case class WhipCount(supportLevel: Option[SupportLevel],
                     count: Int,
                     percentage: Int)
