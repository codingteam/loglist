name := """loglist"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.scalikejdbc" %% "scalikejdbc" % "2.1.2",
  "org.scalikejdbc" %% "scalikejdbc-play-dbplugin-adapter" % "2.3.2",
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41"
)
