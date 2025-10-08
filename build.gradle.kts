plugins {
    alias(libs.plugins.ktlint) apply false
    id("maven-publish")
    kotlin("jvm") version "2.1.20" apply false
    kotlin("multiplatform") version "2.1.20" apply false
}

subprojects {
    apply(plugin = "maven-publish")

    // ktlintプラグインはKotlinプラグインが適用されているプロジェクトのみに適用
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        apply(plugin = libs.plugins.ktlint.get().pluginId)
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
        apply(plugin = libs.plugins.ktlint.get().pluginId)
    }
}

tasks.register<Delete>(name = "clean") {
    delete(rootProject.layout.buildDirectory)
}
