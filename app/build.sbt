inThisBuild(
        Seq(
            scalaVersion := "3.1.2",
            scalacOptions ++= Seq(
                "-feature",
                "-deprecation"
                )
           )
        )


lazy val app = project
    .in(file("."))
.enablePlugins(ScalaJSPlugin)
    .settings(
            scalaVersion := "3.1.2",
            scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
            Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
            Compile / fullOptJS / artifactPath := {
            sys.env.get("FULLOPT_FILE").fold(baseDirectory.value / "dist" / "index.js")(new File(_))
            },
            Compile / fastOptJS / artifactPath := baseDirectory.value / "dist" / "index.js",
            scalacOptions ++= Seq(
                "-Xmax-inlines",
                "64"
                ),
            libraryDependencies ++= Seq(
                "io.circe" %%% "circe" % "0.15.0-M1",
                "io.circe" %%% "circe-generic" % "0.15.0-M1",
              )
            )

