plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.coroutine)
    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}
