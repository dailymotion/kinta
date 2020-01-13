rootProject.name = "kinta"

include(":kinta-cli", ":kinta-lib", "kinta-workflows")

pluginManagement {
    repositories {
        // mavenLocal()
        mavenCentral()
        google()
        jcenter()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.apollographql") {
                useModule("com.apollographql.apollo:apollo-gradle-plugin-incubating:${requested.version}")
            }
            if (requested.id.id == "org.jetbrains.dokka") {
                useModule("org.jetbrains.dokka:dokka-gradle-plugin:${requested.version}")
            }
        }
    }
}