plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
}

dependencies {
    testImplementation(kotlin("test"))
}
