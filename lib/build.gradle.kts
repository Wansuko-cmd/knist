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
                api(project(":io-type:core"))

                implementation(libs.coroutine)
                implementation(libs.serialization)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("perceptron") {
            groupId = libs.versions.lib.group.id.get()
            artifactId = "perceptron"
            version = libs.versions.lib.version.get()
            from(components["kotlin"])
        }
    }
}
