package models.frontend

import models.Chamber

case class WhipCountForChamber(chamber: Chamber,
                               whipCounts: List[WhipCount])
