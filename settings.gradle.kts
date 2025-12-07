@file:Suppress("UnstableApiUsage")

pluginManagement {
    enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
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

include(":io-type:blas")

include(":io-type:blas:base")
include(":io-type:blas:open")
include(":io-type:blas:open:cpp")

include(":io-type:blas:cl")
include(":io-type:blas:cl:cpp")

// include(":deprecated:functional")
// include(":deprecated:optimize")
// include(":deprecated:tensor")
// include(":deprecated:logical")
// include(":deprecated:practical")
