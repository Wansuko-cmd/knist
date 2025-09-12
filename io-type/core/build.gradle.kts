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

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "perceptron"
            version = libs.versions.lib.version.get()
        }
    }
}
