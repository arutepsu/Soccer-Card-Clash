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
  .settings(
    name := "Soccer Card Clash",
    version := "0.1.0-SNAPSHOT",
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    javaOptions += "-Dfile.encoding=UTF-8",
    scalaVersion := scala3Version,
    libraryDependencies += "com.google.inject" % "guice" % "5.1.0",
    libraryDependencies += "net.codingwell" %% "scala-guice" % "5.1.0",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33" excludeAll (
      ExclusionRule(organization = "org.openjfx")
      ),
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.3",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0",

    libraryDependencies ++= (Seq() ++ setJavaFXVersion.value),
  )