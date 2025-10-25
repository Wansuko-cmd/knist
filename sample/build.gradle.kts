plugins {
    kotlin("jvm")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.coroutine)
    implementation(libs.serialization)

    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}
