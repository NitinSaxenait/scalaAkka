ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.7"
scalaVersion := "2.12.7"

val akkaVersion = "2.6.18"
val scalaTestVersion = "3.2.9"

lazy val root = (project in file("."))
  .settings(
    name := "Akka_practice"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion,
)