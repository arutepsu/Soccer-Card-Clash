import sbt._
import Keys._
import sbtassembly.AssemblyPlugin
import sbtassembly.AssemblyPlugin.autoImport._
import org.scoverage.coveralls.CoverallsPlugin

val scala3Version = "3.3.3"
ThisBuild / scalaVersion := scala3Version

ThisBuild / organization := "io.github.arutepsu"
ThisBuild / version := "0.1.4"

ThisBuild / homepage := Some(url("https://github.com/arutepsu/Soccer-Card-Clash"))
ThisBuild / licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/arutepsu/Soccer-Card-Clash"),
    "scm:git:https://github.com/arutepsu/Soccer-Card-Clash.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "arutepsu",
    name  = "arutepsu",
    email = "arutepsu@gmail.com",
    url   = url("https://github.com/arutepsu")
  )
)
ThisBuild / versionScheme := Some("semver-spec")

lazy val coreDeps: Seq[ModuleID] = Seq(
  "com.google.inject" % "guice" % "5.1.0",
  "net.codingwell" %% "scala-guice" % "7.0.0",
  "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
  "javax.inject" % "javax.inject" % "1",
  "com.typesafe.play" %% "play-json" % "2.10.3",
  "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.scalactic" %% "scalactic" % "3.2.14",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test
)

def javafxModules(osName: String, arch: String): Seq[ModuleID] = {
  val javafxVersion = "22"
  val os = osName.toLowerCase

  val classifier = os match {
    case o if o.contains("windows") => "win"
    case o if o.contains("linux") =>
      arch match {
        case "aarch64" => "linux-aarch64"
        case _         => "linux"
      }
    case o if o.contains("mac") || o.contains("darwin") => "mac"
    case _ => throw new UnsupportedOperationException(s"Unsupported OS: $osName / $arch")
  }

  Seq(
    "org.openjfx" % "javafx-base" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-controls" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-fxml" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-graphics" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-media" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-swing" % javafxVersion classifier classifier,
    "org.openjfx" % "javafx-web" % javafxVersion classifier classifier
  )
}

lazy val app = (project in file("."))
  .enablePlugins(CoverallsPlugin, AssemblyPlugin)
  .settings(
    name := "soccer-card-clash-app",

    scalacOptions ++= Seq("-encoding", "UTF-8"),
    javaOptions += "-Dfile.encoding=UTF-8",

    libraryDependencies ++=
      coreDeps ++
        Seq(
          "org.scalafx" %% "scalafx" % "22.0.0-R33" excludeAll
            ExclusionRule(organization = "org.openjfx")
        ) ++
        javafxModules(sys.props("os.name"), sys.props("os.arch")),

    Compile / mainClass := Some("de.htwg.se.soccercardclash.SoccerCardClash"),
    Compile / run / fork := true,
    Compile / run / connectInput := true,

    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "substance", _*) => MergeStrategy.discard
      case PathList("META-INF", "versions", _*)  => MergeStrategy.first
      case PathList("META-INF", "services", _*)  => MergeStrategy.filterDistinctLines
      case "module-info.class"                   => MergeStrategy.discard
      case _                                     => MergeStrategy.first
    },

    publish / skip := true
  )

lazy val corepub = (project in file(".corepub"))
  .settings(
    name := "soccer-card-clash-core",

    Compile / unmanagedSourceDirectories := (app / Compile / unmanagedSourceDirectories).value,
    Test / unmanagedSourceDirectories    := (app / Test / unmanagedSourceDirectories).value,

    Compile / excludeFilter := new FileFilter {
      override def accept(f: File): Boolean = {
        val p = f.getPath.replace('\\', '/')
        p.contains("/de/htwg/se/soccercardclash/view/gui/") ||
        p.contains("/de/htwg/se/soccercardclash/view/tui/") ||
        p.endsWith("/de/htwg/se/soccercardclash/SoccerCardClash.scala") ||
        p.endsWith("/de/htwg/se/soccercardclash/module/SceneModule.scala")
      }
    },

    libraryDependencies ++= coreDeps,

    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },

    publish / skip := false
  )
