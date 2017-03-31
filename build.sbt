name := "EasyRegistration"

version := "1.0"

scalaVersion := "2.12.1"

val AKKA_HTTP_VERSION = "10.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core"       % AKKA_HTTP_VERSION,
  "com.typesafe.akka" %% "akka-http"            % AKKA_HTTP_VERSION,
  "com.typesafe.akka" %% "akka-http-spray-json" % AKKA_HTTP_VERSION,
  "ch.qos.logback"    %  "logback-classic"      % "1.1.3",
  "com.typesafe.akka" %% "akka-slf4j"           % "2.4.17",
  "org.reactivemongo" %% "reactivemongo"        % "0.12.1",
  "com.beachape"      %% "enumeratum"           % "1.5.10"
)
        