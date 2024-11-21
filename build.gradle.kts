// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

    // Google services plugin (if you're using Firebase or Google Play services)
    id("com.google.gms.google-services") version "4.4.2" apply false

    // SonarQube plugin
    id("org.sonarqube") version "6.0.0.5145" apply true
}

// Configure SonarQube properties
sonarqube {
    properties {
        property("sonar.projectKey", "venem_venem-poultry")
        property("sonar.organization", "venem")
        property("sonar.host.url", "https://sonarcloud.io")

        // Additional optional properties
        property("sonar.language", "kotlin") // Specify language if the project is in Kotlin
        property("sonar.sources", "src/main/java") // Specify source directory
        property("sonar.tests", "src/test/java") // Specify test directory
        property("sonar.java.binaries", "build") // Specify binary output directory
    }
}
