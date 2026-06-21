plugins {
    jacoco
}

tasks.register<JacocoReport>("jacocoTestReport") {
    group = "Verification"
    description = "Generates an aggregate report from all subprojects"

    val subprojectsWithJacoco = subprojects.filter { sub ->
        sub.plugins.hasPlugin("jacoco")
    }

    dependsOn(subprojectsWithJacoco.flatMap { it.tasks.withType<Test>() })

    sourceDirectories.setFrom(subprojectsWithJacoco.map { sub ->
        sub.extensions.getByType<SourceSetContainer>().getByName("main").allSource.srcDirs
    })
    classDirectories.setFrom(subprojectsWithJacoco.map { sub ->
        sub.extensions.getByType<SourceSetContainer>().getByName("main").output
    })
    
    // Explicitly use the execution data from the test tasks
    executionData.setFrom(subprojectsWithJacoco.flatMap { sub ->
        sub.tasks.withType<Test>().map { it.extensions.getByType<JacocoTaskExtension>().destinationFile }
    })

    reports {
        html.required.set(false)
        xml.required.set(true)
        csv.required.set(true)
        //html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/root"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/root/report.xml"))
        csv.outputLocation.set(layout.buildDirectory.file("reports/jacoco/root/report.csv"))
    }
}
