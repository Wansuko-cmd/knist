plugins {
    kotlin("multiplatform") version "2.1.20"
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutine)
            }
        }
    }
}
