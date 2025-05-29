val scala3Version = "3.3.3"

val setJavaFXVersion = settingKey[Seq[ModuleID]](
  "Sets the JavaFX version and classifier based on the system architecture"
)

setJavaFXVersion := {
  val arch = sys.props("os.arch")
  val osName = sys.props("os.name").toLowerCase
  val javafxVersion = "22"
  val javafxClassifier = osName match {
    case os if os.contains("windows") => "win"
    case os if os.contains("linux") =>
      arch match {
        case "aarch64" => "linux-aarch64"
        case _         => "linux"
      }
    case _ => throw new UnsupportedOperationException("Unsupported OS")
  }

  Seq(
    "org.openjfx" % "javafx-base" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-controls" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-fxml" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-graphics" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-media" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-swing" % javafxVersion classifier javafxClassifier,
    "org.openjfx" % "javafx-web" % javafxVersion classifier javafxClassifier
  )
}

lazy val root = project
  .in(file("."))
  .enablePlugins(CoverallsPlugin, AssemblyPlugin)
  .settings(
    name := "Soccer Card Clash",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    javaOptions += "-Dfile.encoding=UTF-8",

    libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0",
    libraryDependencies += "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
    libraryDependencies += "javax.inject" % "javax.inject" % "1",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % Test,
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.3",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
    libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33" excludeAll (
      ExclusionRule(organization = "org.openjfx")
      ),
    libraryDependencies ++= setJavaFXVersion.value,

    coverageEnabled := true,
    coverageHighlighting := true,
    coverageFailOnMinimum := false,
    coverageMinimumStmtTotal := 40,

    addCommandAlias("fullTest", ";clean;coverage;test;coverageReport;coverageAggregate;coveralls")
  )

import sbtassembly.AssemblyPlugin.autoImport._

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "substance", _*) => MergeStrategy.discard
  case PathList("META-INF", "versions", _*)  => MergeStrategy.first
  case PathList("META-INF", "services", _*)  => MergeStrategy.filterDistinctLines
  case "module-info.class"                   => MergeStrategy.discard
  case _                                     => MergeStrategy.first
}
