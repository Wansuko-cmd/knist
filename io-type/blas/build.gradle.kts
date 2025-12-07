plugins {
    kotlin("multiplatform")
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.ioType.blas.base)
                api(projects.ioType.blas.open)
                api(projects.ioType.blas.cl)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
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
