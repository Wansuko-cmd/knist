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
include(":io-type")

include(":buffer")
include(":buffer:base")
include(":buffer:open")
include(":buffer:open:cpp")

include(":buffer:cl")
include(":buffer:cl:cpp")

// include(":deprecated:functional")
// include(":deprecated:optimize")
// include(":deprecated:tensor")
// include(":deprecated:logical")
// include(":deprecated:practical")
