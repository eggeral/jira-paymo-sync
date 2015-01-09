organization := "software.egger"

name := "jira-paymo-sync"

version := "1.0"

scalaVersion := "2.11.5"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/repository/public/"

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "0.9.5",
  "com.atlassian.jira" % "jira-rest-java-client" % "2.0.0-m2"
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.17"
)
