@file:Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "perceptron"

include(":lib")
include(":lib2")
include(":sample")

include(":deprecated:functional")
include(":deprecated:optimize")
include(":deprecated:tensor")
include(":deprecated:logical")
