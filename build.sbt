name := """ScalaChallenge"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

val akkaVersion = "2.3.6"
val sprayVersion = "1.3.1"
val sparkVersion = "1.5.2"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.2"
    exclude("org.slf4j", "slf4j-log4j12")

  ,"ch.qos.logback" % "logback-classic" % "1.1.2"

  ,"com.sksamuel.elastic4s" %% "elastic4s-core" % "1.7.5"

  ,"org.json4s" %% "json4s-jackson" % "3.2.11"
  ,"org.json4s" %% "json4s-ext" % "3.2.11"

  ,"com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
  ,"com.typesafe.akka" %% "akka-actor" % akkaVersion
  ,"com.typesafe.akka" %% "akka-slf4j" % akkaVersion

  ,"io.spray" %% "spray-routing" % sprayVersion
  ,"io.spray" %% "spray-can" % sprayVersion
  ,"io.spray" %% "spray-httpx" % sprayVersion
  ,"io.spray" %% "spray-testkit" % sprayVersion % "test"

  ,"org.apache.spark" %% "spark-core" % sparkVersion //% "provided"

  ,"org.apache.spark" % "spark-mllib_2.11" % sparkVersion //% "provided"

  ,"org.apache.spark" % "spark-sql_2.11" % sparkVersion //% "provided"

  ,"org.apache.spark" % "spark-streaming-kafka_2.11" % sparkVersion

 ,"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.2"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
