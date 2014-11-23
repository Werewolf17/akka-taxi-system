name := "TaxiSystem"

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= {
  val akkaV = "2.3.7"
  val scalatestV = "2.2.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" %% "scalatest" % scalatestV % "test"
  )
}

