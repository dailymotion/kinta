rootProject.name = "kinta"

include(":kinta-cli", ":kinta-lib", "kinta-workflows", "console")

pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.apollographql") {
                useModule("com.apollographql.apollo:apollo-gradle-plugin:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.dokka") {
                useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
            }
        }
    }
}