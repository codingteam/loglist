ThisBuild / version := "2.0.0"
ThisBuild / scalaVersion := "2.13.3"

lazy val scalajs = (project in file("scalajs"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSWeb)
  .settings(scalajsSettings)

lazy val scalajvm = (project in file("scalajvm"))
  .enablePlugins(PlayScala)
  .enablePlugins(SbtWeb)
  .settings(scalajvmSettings)

lazy val scalajsSettings = Seq(
  name := "scalajs",
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "1.1.0"
  ) ++ Seq(
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser"
  ).map(_ % "0.13.0"),
  scalacOptions ++= sharedScalacOptions,
  scalaJSUseMainModuleInitializer := true,
  scalaJSStage := FullOptStage
) ++ sharedDirectorySettings

lazy val scalajvmSettings = Seq(
  name := "scalajvm",
  libraryDependencies ++= Seq(
    guice,
    evolutions,
    jdbc,
    specs2 % Test,
    "org.scalikejdbc" %% "scalikejdbc" % "3.5.0",
    "org.scalikejdbc" %% "scalikejdbc-config" % "3.5.0",
    "org.scalikejdbc" %% "scalikejdbc-play-initializer" % "2.8.0-scalikejdbc-3.5",
    "org.postgresql" % "postgresql" % "9.3-1104-jdbc41",
    "jakarta.mail" % "jakarta.mail-api" % "1.6.5",
    "com.sun.mail" % "smtp" % "1.6.5",
    "org.scalaj" %% "scalaj-http" % "2.4.2",
    "com.vmunier" %% "scalajs-scripts" % "1.1.4"
  ) ++ Seq(
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % "0.13.0"),
  scalacOptions ++= sharedScalacOptions,
  scalaJSProjects := Seq(scalajs),
  pipelineStages in Assets := Seq(scalaJSPipeline)
) ++ sharedDirectorySettings

lazy val sharedScalacOptions = Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
)

val sharedSrcDir = "scala"

lazy val sharedDirectorySettings = Seq(
  unmanagedSourceDirectories in Compile += (ThisBuild / baseDirectory).value / sharedSrcDir / "src" / "main" / "scala",
  unmanagedSourceDirectories in Test += (ThisBuild / baseDirectory).value / sharedSrcDir / "src" / "test" / "scala",
)
