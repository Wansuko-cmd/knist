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
                implementation("org.jetbrains.kotlinx:multik-core:0.2.3")
                implementation("org.jetbrains.kotlinx:multik-default:0.2.3")
            }
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
