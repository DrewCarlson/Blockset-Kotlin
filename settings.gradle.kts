pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

rootProject.name = "Blockset-Kotlin"

enableFeaturePreview("GRADLE_METADATA")

include(":cli")
