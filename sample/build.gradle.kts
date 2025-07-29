plugins {
    kotlin("jvm") version "2.1.20"
}

dependencies {
    implementation(project(":deprecated:practical"))

    implementation(libs.coroutine)
    testImplementation(libs.bundles.test)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}
