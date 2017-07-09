import sbt._
import Keys._
import play.Play._
import play.Play.autoImport._
import PlayKeys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import com.typesafe.sbt.packager.universal.UniversalKeys
import com.typesafe.sbteclipse.core.EclipsePlugin.EclipseKeys

object ApplicationBuild extends Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  override def rootProject = Some(scalajvm)

  val sharedSrcDir = "scala"

  lazy val scalajvm = Project(
    id = "scalajvm",
    base = file("scalajvm")
  ) enablePlugins (play.PlayScala) settings (scalajvmSettings: _*) aggregate (scalajs)

  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*)

  lazy val sharedScala = Project(
    id = "sharedScala",
    base = file(sharedSrcDir)
  ) settings (sharedScalaSettings: _*)

  lazy val scalajvmSettings =
    Seq(
      name := "loglist-jvm",
      version := Versions.app,
      scalaVersion := Versions.scala,
      scalajsOutputDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
      compile in Compile <<= (compile in Compile) dependsOn (
        fullOptJS in (scalajs, Compile),
        packageLauncher in (scalajs, Compile)),
      scalacOptions in Compile ++= Seq("-unchecked", "-deprecation", "-feature"),
      javacOptions in Compile ++= Seq("-source", "1.7", "-target", "1.7"),
      dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),
      libraryDependencies ++= Dependencies.scalajvm,
      commands += preStartCommand,
      EclipseKeys.skipParents in ThisBuild := false
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, packageLauncher, fastOptJS, fullOptJS) map { packageJSKey =>
        crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
    ) ++ sharedDirectorySettings

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "loglist-scalajs",
      version := Versions.app,
      scalaVersion := Versions.scala,
      persistLauncher := true,
      persistLauncher in Test := false,
      resolvers += Resolver.sonatypeRepo("releases"),
      libraryDependencies ++= Seq(
        "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % "0.6",
        "com.lihaoyi" %%% "upickle" % "0.2.5"
      ) ++ Dependencies.scalajs
    ) ++ sharedDirectorySettings

  lazy val sharedScalaSettings =
    Seq(
      name := "loglist-shared",
      EclipseKeys.skipProject := true,
      libraryDependencies ++= Dependencies.shared
    )

  lazy val sharedDirectorySettings = Seq(
    unmanagedSourceDirectories in Compile += new File((file(".") / sharedSrcDir / "src" / "main" / "scala").getCanonicalPath),
    unmanagedSourceDirectories in Test += new File((file(".") / sharedSrcDir / "src" / "test" / "scala").getCanonicalPath),
    unmanagedResourceDirectories in Compile += file(".") / sharedSrcDir / "src" / "main" / "resources",
    unmanagedResourceDirectories in Test += file(".") / sharedSrcDir / "src" / "test" / "resources"
  )

  // Use reflection to rename the 'start' command to 'play-start'
  Option(play.Play.playStartCommand.getClass.getDeclaredField("name")) map { field =>
    field.setAccessible(true)
    field.set(playStartCommand, "play-start")
  }

  // The new 'start' command optimises the JS before calling the Play 'start' renamed 'play-start'
  val preStartCommand = Command.args("start", "<port>") { (state: State, args: Seq[String]) =>
    Project.runTask(fullOptJS in (scalajs, Compile), state)
    state.copy(remainingCommands = ("play-start " + args.mkString(" ")) +: state.remainingCommands)
  }
}

object Dependencies {
  val shared = Seq()

  val scalajvm = Seq(
    jdbc,
    cache,
    "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
    "com.netaporter" %% "scala-uri" % "0.4.4",
    "org.scalikejdbc" %% "scalikejdbc" % "2.1.2",
    "org.scalikejdbc" %% "scalikejdbc-play-dbplugin-adapter" % "2.3.2",
    "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
    "com.lihaoyi" %% "upickle" % "0.2.5",
    "javax.mail" % "mail" % "1.5.0-b01"
  ) ++ shared

  val scalajs = Seq() ++ shared
}

object Versions {
  val app = "1.5.0"
  val scala = "2.11.2"
}
