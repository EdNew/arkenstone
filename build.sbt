lazy val commonSettings = Seq(
  organization := "com.oneapm",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "arkenstone"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.2",
  "com.typesafe.akka" %% "akka-stream" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.2",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.2" % "test"
)

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit-experimental" % "2.4.2-RC3"

libraryDependencies += "org.scalactic" %% "scalactic" % "2.2.6"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"
