plugins {
    kotlin("jvm")
    alias(libs.plugins.serialization)
    application
}

dependencies {
    implementation(projects.lib)

    implementation(libs.coroutine)
    implementation(libs.serialization)

    implementation(libs.okio)

    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test"))
}

application {
    mainClass = "MainKt"
}

tasks.test {
    minHeapSize = "256M"
    maxHeapSize = "${1024 * 8}M"
    jvmArgs = listOf("-XX:MaxMetaspaceSize=1024M")
    useJUnit()
}
