enablePlugins(JavaAppPackaging)

name := "CompanyDataCraler"
version := "1.0"
scalaVersion := "2.13.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpV   = "10.1.12"
  val akkaV       = "2.6.8"
  val scalaTestV  = "3.2.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.1",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "org.scalatest"     %% "scalatest" % scalaTestV % Test
  )
}

Revolver.settings
