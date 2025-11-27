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

include(":sample")
include(":lib")
include(":io-type:core")
include(":io-type:batch")

include(":io-type:blas:core")
include(":io-type:blas:base")
include(":io-type:blas:open")
include(":io-type:blas:open:cpp")

// include(":deprecated:functional")
// include(":deprecated:optimize")
// include(":deprecated:tensor")
// include(":deprecated:logical")
// include(":deprecated:practical")
