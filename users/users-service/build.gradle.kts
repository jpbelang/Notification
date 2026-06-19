plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.library)
}

micronaut {
    testRuntime("kotest5")
    processing {
        incremental(true)
        annotations("ca.notification.*")
    }
}

dependencies {
    implementation(libs.micronaut.runtime)
    implementation(libs.micronaut.function.aws)
    implementation(libs.micronaut.aws.lambda.events.serde)
    implementation(libs.micronaut.serde.jackson)
    implementation(libs.awsLambdaEvents)
    ksp(libs.micronaut.inject.kotlin)
    ksp(libs.micronaut.serde.processor)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.micronaut.test.kotest5)
    testImplementation(kotlin("test"))
}
