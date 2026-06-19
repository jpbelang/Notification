plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    application
}

application {
    mainClass.set("ca.notification.users.infrastructure.cdk.UsersInfrastructureAppKt")
}


dependencies {
    implementation(project(":users:users-service"))
    implementation(libs.bundles.awsCdk)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(kotlin("test"))
}
