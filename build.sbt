import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

// ---------------------------------------------------------------------------
//  Main settings
// ---------------------------------------------------------------------------

name := "streamz"

organization in ThisBuild := "com.github.krasserm"

version in ThisBuild := "0.12-CE3"

crossScalaVersions in ThisBuild := Seq("2.11.12", "2.12.10", "2.13.7")

scalaVersion in ThisBuild := "2.13.7"

ThisBuild / libraryDependencies  ++= Seq(
  "org.scalatest" %% "scalatest-wordspec" % Version.Scalatest % "test",
  "org.scalatest" %% "scalatest-shouldmatchers" % Version.Scalatest % "test",
)

// No need for `sbt doc` to fail on warnings
val docSettings = Compile / doc / scalacOptions --= Seq("-Xfatal-warnings", "-Xlint:nullary-override")

publishMavenStyle := true

publishTo in ThisBuild := {
  val nexus = "http://nexus.market.local/repository/maven-"
  if (isSnapshot.value)
    Some("snapshots".at(nexus + "snapshots/"))
  else
    Some("releases".at(nexus + "releases/"))
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishArtifact in Test := false

// ---------------------------------------------------------------------------
//  Code formatter settings
// ---------------------------------------------------------------------------

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DanglingCloseParenthesis, Preserve)
  .setPreference(DoubleIndentConstructorArguments, false)

// ---------------------------------------------------------------------------
//  License header settings
// ---------------------------------------------------------------------------

lazy val header = HeaderLicense.ALv2("2014 - 2019", "the original author or authors.")

lazy val headerSettings = Seq(
  headerLicense := Some(header)
)

// ---------------------------------------------------------------------------
//  Projects
// ---------------------------------------------------------------------------

lazy val root = project.in(file("."))
  .aggregate(camelContext, camelAkka, camelFs2, converter, examples)
  .settings(
    unidocProjectFilter in (ScalaUnidoc, unidoc) := inAnyProject -- inProjects(examples),
    docSettings
  )
  .enablePlugins(ScalaUnidocPlugin)

lazy val camelContext = project.in(file("streamz-camel-context"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(headerSettings, docSettings)

lazy val camelAkka = project.in(file("streamz-camel-akka"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(headerSettings, docSettings)
  .dependsOn(camelContext)

lazy val camelFs2 = project.in(file("streamz-camel-fs2"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(headerSettings, docSettings)
  .dependsOn(camelContext)

lazy val converter = project.in(file("streamz-converter"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(
    headerSettings,
    docSettings,
    scalacOptions --= Seq("-Xfatal-warnings", "-Xlint:nullary-override")
  )

lazy val examples = project.in(file("streamz-examples"))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(headerSettings, docSettings)
  .dependsOn(camelAkka, camelFs2, converter)