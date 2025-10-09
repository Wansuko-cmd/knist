plugins {
    kotlin("multiplatform")
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm {
        val jvmProcessResources by tasks.getting {
            dependsOn(":io-type:blas:cpp:cmakeBuild")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutine)
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
            groupId = libs.versions.lib.group.id.get()
            artifactId = "perceptron"
            version = libs.versions.lib.version.get()
        }
    }
}
