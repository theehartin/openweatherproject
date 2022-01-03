import Dependencies._

ThisBuild / scalaVersion     := "2.11.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "OpenWeatherProject",
    libraryDependencies += "org.apache.hadoop" % "hadoop-client" % "2.7.0"
    ,libraryDependencies += "org.apache.kafka" %% "kafka" % "2.1.0"  
    ,libraryDependencies += "net.liftweb" %% "lift-json" % "2.6"
    ,libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.3"
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
