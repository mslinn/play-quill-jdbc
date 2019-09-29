name := """play-quill-jdbc"""

version := "0.2.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  jdbc,
  //cacheApI,
  evolutions,
  ws,
  "com.typesafe.play"      %% "twirl-api"            % "1.4.2"   withSources(),
  "com.h2database"         %  "h2"                   % "1.4.199" withSources(),
  "io.getquill"            %% "quill-jdbc"           % "3.4.9"   withSources(),
  //
  "org.scalatestplus.play" %% "scalatestplus-play"   % "4.0.3"   % Test
)

resolvers ++= Seq(
)

routesGenerator := InjectedRoutesGenerator
