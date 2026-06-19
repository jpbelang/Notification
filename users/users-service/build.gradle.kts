plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(kotlin("test"))
}
