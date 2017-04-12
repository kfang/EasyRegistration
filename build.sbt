name := "easy-registration"

organization := "com.github.kfang.easyregistration"

version := "0.0.1"

scalaVersion := "2.12.1"

val AKKA_HTTP_VERSION = "10.0.5"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core"       % AKKA_HTTP_VERSION,
  "com.typesafe.akka" %% "akka-http"            % AKKA_HTTP_VERSION,
  "com.typesafe.akka" %% "akka-http-spray-json" % AKKA_HTTP_VERSION,
  "ch.qos.logback"    %  "logback-classic"      % "1.1.3",
  "com.typesafe.akka" %% "akka-slf4j"           % "2.4.17",
  "org.reactivemongo" %% "reactivemongo"        % "0.12.1",
  "com.beachape"      %% "enumeratum"           % "1.5.10",
  "ch.megard"         %% "akka-http-cors"       % "0.1.11",
  "org.apache.commons" % "commons-email"        % "1.4"
)

enablePlugins(DockerPlugin)

dockerfile in docker := {
  val jarFile = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = mainClass.in(Compile, packageBin).value.getOrElse(sys.error("Expected exactly one main class"))
  val jarTarget = s"/app/${jarFile.getName}"

  // Make a colon separated classpath with the JAR file
  //  val classpathString = classpath.files.map("/app/" + _.getName).mkString(":") + ":" + jarTarget
  val cp = s"""$jarTarget:/app/*"""

  new Dockerfile {
    // Base image
    from("java")
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    //expose the port
    expose(8080)
    // On launch run Java with the classpath and the main class
    entryPointShell("java", "-cp", cp, "${JAVA_OPTS}", mainclass)
  }
}

imageNames in docker := Seq(
  ImageName(
    namespace = Some("kfang"),
    repository = name.value,
    tag = Some("v" + version.value)
  ),
  ImageName(
    namespace = Some("kfang"),
    repository = name.value,
    tag = Some("latest")
  )
)
