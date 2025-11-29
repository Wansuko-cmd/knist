plugins {
    kotlin("jvm")
    alias(libs.plugins.serialization)
}

dependencies {
    implementation(projects.lib)

    implementation(libs.coroutine)
    implementation(libs.serialization)

    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    minHeapSize = "256M"
    maxHeapSize = "${1024 * 8}M"
    jvmArgs = listOf("-XX:MaxMetaspaceSize=1024M")
    useJUnit()
}
