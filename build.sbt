// Dependencies are needed for Scala Steward to check if there are newer versions
val zioVersion            = "2.0.13"
lazy val It = config("it").extend(Test)

val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .configs(It)
  .settings(
    name           := "mind-the-gap-talk",
    scriptedLaunchOpts ++= List(
      "-Xms1024m",
      "-Xmx1024m",
      "-XX:ReservedCodeCacheSize=128m",
      "-Xss2m",
      "-Dfile.encoding=UTF-8"
    ),
    resolvers += Resolver.url(
      "typesafe",
      url("https://repo.typesafe.com/typesafe/ivy-releases/")
    )(Resolver.ivyStylePatterns),
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    libraryDependencies ++= Seq(
      // zio-http
      "dev.zio" %% "zio" % zioVersion,
      "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.2",
      "org.slf4j" % "slf4j-api" % "2.0.5",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
