plugins {
    id("maven-publish")
}

subprojects {
    apply(plugin = "maven-publish")
}

tasks.register<Delete>(name = "clean") {
    delete(rootProject.layout.buildDirectory)
}
