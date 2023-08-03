plugins {
  `java-library`
  `maven-publish`
}

version = version.let { conformVersionToMavenConvention("${it}") }

fun conformVersionToMavenConvention(it: String): String =
  if (project.hasProperty("releaseBuild")) "${it}" else "${it}-SNAPSHOT"

val junitVersion by extra { "5.8.2" }
val junitQuickcheckVersion by extra { "1.0" }
val slf4jVersion by extra { "1.7.36" }

dependencies {
  testImplementation("com.pholser:junit-quickcheck-core:${junitQuickcheckVersion}")
  testImplementation("com.pholser:junit-quickcheck-generators:${junitQuickcheckVersion}")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${junitVersion}")
  testRuntimeOnly("org.slf4j:slf4j-simple:${slf4jVersion}")
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

  filter {
    if (isSnapshotBuild) {
      excludeTestsMatching("*stepwise*")
    }

    if (project.hasProperty("ignoreKnownFailures")) {
      excludeTestsMatching("*SetMultimapPropertiesTestSuite\$PersistentTrieSetMultimapTest.sizeAfterInsertKeyValues")
      excludeTestsMatching("*SetMultimapPropertiesTestSuite\$PersistentTrieSetMultimapTest.sizeAfterTransientInsertKeyValues")
      excludeTestsMatching("*SetMultimapPropertiesTestSuite\$PersistentBidirectionalTrieSetMultimapTest.sizeAfterInsertKeyValues")
    }
  }
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
