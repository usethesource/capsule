plugins {
  `java-library`
}

val majorMinorPatchVersion = "0.7.1"
val isSnapshotBuild = !project.hasProperty("isReleaseBuild")

version = majorMinorPatchVersion.let {
  if (isSnapshotBuild) "${it}-SNAPSHOT" else "${it}"
}

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
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}

tasks.compileJava {
  options.release.set(11)
}

tasks.jar {
  manifest {
    attributes["Automatic-Module-Name"] = "io.usethesource.capsule"
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
