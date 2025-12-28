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
rootProject.name = "knist"

include(":sample")

include(":network")

include(":io-type")

include(":buffer")
include(":buffer:base")
include(":buffer:cpu")
include(":buffer:cpu:cpp")
include(":buffer:gpu")
include(":buffer:gpu:cpp")

include(":buffer:open")
include(":buffer:open:cpp")
include(":buffer:cl")
include(":buffer:cl:cpp")
