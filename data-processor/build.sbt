scalaVersion := "2.10.3"

resolvers += "Typesafe releases" at "http://repo.typesafe.com/typesafe/repo"

libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.1",
    "com.typesafe" %% "scalalogging-slf4j" % "1.1.0",
    "com.github.seratch" %% "scalikejdbc" % "[1.6,)",
    "com.github.seratch" %% "scalikejdbc-config" % "[1.6,)",
    "com.typesafe.play" %% "play" % "2.2.1",
    "com.typesafe.play" %% "anorm" % "2.2.1",
    "postgresql" % "postgresql" % "9.1-901.jdbc4"
)

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)
