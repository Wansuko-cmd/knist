plugins {
    kotlin("multiplatform") version "2.1.20"
    alias(libs.plugins.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutine)
                implementation(libs.serialization)
            }
        }
    }
}
