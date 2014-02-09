name := "website"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.3.0"
)     

play.Project.playScalaSettings
