package models.frontend

import models.SupportLevel

case class WhipCount(supportLevel: SupportLevel,
                     count: Int,
                     percentage: Int)
