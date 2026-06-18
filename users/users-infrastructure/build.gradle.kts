plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    application
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.library)
}

application {
    mainClass.set("org.example.users.infrastructure.cdk.UsersInfrastructureAppKt")
}

micronaut {
    runtime("lambda_java")
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("org.example.*")
    }
}

dependencies {
    implementation(project(":users:users-service"))
    implementation(libs.micronaut.runtime)
    implementation(libs.micronaut.function.aws)
    implementation(libs.micronaut.aws.lambda.events.serde)
    implementation(libs.micronaut.serde.jackson)
    implementation(libs.awsLambdaEvents)
    ksp(libs.micronaut.inject.kotlin)
    ksp(libs.micronaut.serde.processor)

    implementation(libs.bundles.awsCdk)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.micronaut.test.kotest5)
    testImplementation(kotlin("test"))
}
