plugins {
  `java-library`
  `maven-publish`
  id("me.champeau.jmh") version "0.7.1"
}

version = version.let { conformVersionToMavenConvention("${it}") }

fun conformVersionToMavenConvention(it: String): String =
  if (project.hasProperty("releaseBuild")) "${it}" else "${it}-SNAPSHOT"

val junitVersion by extra { "5.10.0" }
val junitQuickcheckVersion by extra { "1.0" }
val slf4jVersion by extra { "2.0.9" }

dependencies {
  testImplementation("com.pholser:junit-quickcheck-core:${junitQuickcheckVersion}")
  testImplementation("com.pholser:junit-quickcheck-generators:${junitQuickcheckVersion}")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${junitVersion}")
  testRuntimeOnly("org.slf4j:slf4j-simple:${slf4jVersion}")

  jmh(group = "com.github.msteindorfer", name = "memory-measurer", version = "5be4fe7")

  /*** 3rd party JVM languages with persistent collection libraries ***/
  jmh(group = "org.clojure", name = "clojure", version = "1.11.1")
  jmh(group = "org.scala-lang", name = "scala-library", version = "2.13.11")
  jmh(group = "org.scala-lang.modules", name = "scala-collection-contrib_2.13", version = "0.3.0")

  /*** 3rd party Java libraries with persistent data structures ***/
  jmh(group = "com.github.andrewoma.dexx", name = "collection", version = "0.7")
  jmh(group = "io.lacuna", name = "bifurcan", version = "0.2.0-alpha6")
  jmh(group = "io.vavr", name = "vavr", version = "0.10.4")
  jmh(group = "org.organicdesign", name = "Paguro", version = "3.10.3")
  jmh(group = "org.pcollections", name = "pcollections", version = "4.0.1")

  /*** 3rd party Java libraries with immutable data structures and/or specialized primitive collections ***/
  jmh(group = "com.goldmansachs", name = "gs-collections-api", version = "7.0.3")
  jmh(group = "com.goldmansachs", name = "gs-collections", version = "7.0.3")
  jmh(group = "com.google.guava", name = "guava", version = "31.1-jre")
  jmh(group = "it.unimi.dsi", name = "fastutil", version = "8.5.12")
  jmh(group = "net.sf.trove4j", name = "trove4j", version = "3.0.3")
  jmh(group = "org.apache.mahout", name = "mahout-math", version = "0.13.0")
}

repositories {
  maven {
    url = uri("https://jitpack.io") // required to resolve `com.github.msteindorfer:memory-measurer:<version>`
  }
}

tasks.register("memoryMeasurerLocation") {
  val jarFile = configurations.jmh.get().files.first { it.name.contains("memory-measurer") }
  println(jarFile.toString())
}

java {
  withSourcesJar()

  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.compileJava {
  options.release.set(11)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "${rootProject.group}.${rootProject.name}"
  }
}

tasks.test {
  maxParallelForks = 8
  maxHeapSize = "1G"
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = System.getenv("CAPSULE_MAVEN_PUBLICATION_GROUP_ID") ?: groupId
      artifactId = System.getenv("CAPSULE_MAVEN_PUBLICATION_ARTIFACT_ID") ?: artifactId
      version = System.getenv("CAPSULE_MAVEN_PUBLICATION_VERSION")?.let { conformVersionToMavenConvention("${it}") } ?: version

      pom {
        licenses {
          license {
            name.set("BSD 2-Clause Simplified License")
            url.set("https://raw.githubusercontent.com/usethesource/capsule/main/LICENSE")
          }
        }
      }

      from(components["java"])
    }
  }
}
