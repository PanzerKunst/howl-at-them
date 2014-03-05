package models

object SupportLevel extends Enumeration {
  type SupportLevel = Value

  val Supportive = Value("Supportive")
  val NeedsConvincing = Value("Needs convincing")
  val NotSupportive = Value("Not supportive")
}
