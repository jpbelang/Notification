plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    application
}

application {
    mainClass.set("org.example.users.infrastructure.cdk.UsersInfrastructureAppKt")
}

dependencies {
    implementation(libs.bundles.awsCdk)
    testImplementation(kotlin("test"))
}
