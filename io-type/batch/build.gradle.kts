plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":io-type:blas"))
                implementation(project(":io-type:core"))

                implementation(libs.coroutine)
                implementation(libs.serialization)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
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
            groupId =
                libs.versions.lib.group.id
                    .get()
            artifactId = "perceptron"
            version =
                libs.versions.lib.version
                    .get()
        }
    }
}
