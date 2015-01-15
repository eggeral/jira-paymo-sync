organization := "software.egger"

name := "jira-paymo-sync"

version := "1.0"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-feature")

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/repository/public/"

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "0.9.5",
  "com.atlassian.jira" % "jira-rest-java-client-core" % "2.0.0-m31",
  "org.slf4j" % "slf4j-simple" % "1.7.10"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.17"
)
