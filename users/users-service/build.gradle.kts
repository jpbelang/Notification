plugins {
    // Apply the shared build logic from a convention plugin.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.ksp)
    alias(libs.plugins.micronaut.library)
    alias(libs.plugins.shadow)
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
    implementation(libs.micronaut.function.aws.api.proxy)
    implementation(libs.micronaut.aws.lambda.events.serde)
    implementation(libs.micronaut.serde.jackson)
    implementation(libs.awsLambdaEvents)
    implementation(libs.aws.sdk.dynamodb)
    implementation(libs.aws.sdk.cognito)
    implementation(libs.micronaut.aws.sdk.v2)
    ksp(libs.micronaut.inject.kotlin)
    ksp(libs.micronaut.serde.processor)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.micronaut.test.kotest5)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("all")
    archiveFileName.set("users-service-all.jar")
    mergeServiceFiles()
}
