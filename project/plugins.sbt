resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.11")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.1")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.3.1")

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.0")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")