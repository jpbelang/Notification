plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    application
}

application {
    mainClass.set("ca.notification.organisations.infrastructure.cdk.OrganisationsInfrastructureAppKt")
}

dependencies {
    implementation(project(":organisations:organisations-service"))
    implementation(libs.bundles.awsCdk)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(kotlin("test"))
}
