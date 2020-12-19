name := "Aishwarya_Sahani_hw1"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Seq(
  "org.cloudsimplus" % "cloudsim-plus" % "5.4.3",
  "com.typesafe" % "config" % "1.4.0",
  "org.slf4j" % "slf4j-api" % "1.7.30",
//  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.novocode" % "junit-interface" % "0.8" % "test->default"
)