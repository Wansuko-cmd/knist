plugins {
    kotlin("multiplatform")
}

val nativeLibFile = projectDir.resolve("cpp/build/libJBLAS.dylib")
val jvmResourcesDir = projectDir.resolve("src/jvmMain/resources")

val copyNativeToResources by tasks.registering(Copy::class) {
    group = "build"
    description = "Copy libJBLAS.dylib to JVM resources"

    dependsOn(":io-type:blas:cpp:cmakeBuild")
    from(nativeLibFile)
    into(jvmResourcesDir)
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
