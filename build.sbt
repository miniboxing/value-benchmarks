
name := "valium-benchmarks"

scalaVersion in Global := "2.11.1"

version := "0.1-SNAPSHOT"

libraryDependencies += "com.github.axel22" %% "scalameter" % "0.5-M2"

testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework")

parallelExecution in Test := false
