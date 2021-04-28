import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version(Versions.kotlin).apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version(Versions.kotlin).apply(false)
    id("com.apollographql.apollo").version(Versions.apollo).apply(false)
    id("org.jetbrains.dokka").version(Versions.dokka).apply(false)
}

fun isTag() = !System.getenv("TRAVIS_TAG").isNullOrBlank()

fun isMaster(): Boolean {
    val branch = System.getenv("TRAVIS_BRANCH")
    val pullRequest = System.getenv("TRAVIS_PULL_REQUEST") as String?

    return (pullRequest?.isBlank() == true || pullRequest == "false") && branch == "master"
}


version = "0.1.13"

subprojects {
    repositories {
        jcenter()
        maven {
            url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
        }
    }

    tasks.withType<KotlinCompile> {
        plugins.withId("org.jetbrains.kotlin.plugin.serialization") {
            kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.serialization.ImplicitReflectionSerializer"
            kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlinx.serialization.UnstableDefault"
        }
        kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
        kotlinOptions {
            allWarningsAsErrors = true
        }
    }

    afterEvaluate {
        this.configure<JavaPluginExtension> {
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    group = "com.dailymotion.kinta"
    version = rootProject.version

    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")

    tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
        configuration {
            reportUndocumented = false
            outputFormat = "gfm"

            outputDirectory = "$rootDir/build/kdoc"

            perPackageOption {
                // uncomment when/if https://github.com/Kotlin/dokka/pull/598 is merged
                // matchingRegex = ".*\\.internal"
                suppress = true
            }
        }
    }

    afterEvaluate {
        configureMavenPublish()
    }

    tasks.register<Task>("deployArtifactsIfNeeded") {
        if (isTag()) {
            project.logger.lifecycle("Upload to Bintray needed.")
            dependsOn("publishDefaultPublicationToBintrayRepository")
        } else if (isMaster()) {
            project.logger.lifecycle("Upload to OJO needed.")
            dependsOn("publishDefaultPublicationToOjoRepository")
        }
    }
}

fun Project.configureMavenPublish() {
    val javaPluginConvention = project.convention.findPlugin(JavaPluginConvention::class.java)
    val sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
        archiveClassifier.set("sources")
        from(javaPluginConvention?.sourceSets?.get("main")?.allSource)
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("default") {
                from(components.findByName("java"))

                artifact(sourcesJarTaskProvider.get())

                pom {
                    groupId = group.toString()
                    artifactId = findProperty("POM_ARTIFACT_ID") as String?
                    version = project.version as String?

                    name.set(findProperty("POM_NAME") as String?)
                    packaging = "jar"
                    description.set(findProperty("POM_DESCRIPTION") as String?)
                    url.set("https://github.com/dailymotion/kinta")

                    scm {
                        url.set("https://github.com/dailymotion/kinta")
                        connection.set("https://github.com/dailymotion/kinta")
                        developerConnection.set("https://github.com/dailymotion/kinta")
                    }

                    licenses {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }

                    developers {
                        developer {
                            id.set("Dailymotion")
                            name.set("Dailymotion")
                        }
                    }
                }
            }
        }

        repositories {
            maven {
                name = "bintray"
                url = uri("https://api.bintray.com/maven/dailymotion/com.dailymotion.kinta/kinta/;publish=1;override=1")
                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_API_KEY")
                }
            }
            maven {
                name = "ojo"
                url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
                credentials {
                    username = System.getenv("BINTRAY_USER")
                    password = System.getenv("BINTRAY_API_KEY")
                }
            }
        }
    }
}

apply(from = "docs.gradle.kts")

tasks.register("deployDocsIfNeeded") {
    if (isMaster()) {
        dependsOn("deployDocs")
    }
}

tasks.register("deployArchivesIfNeeded") {
    if (isTag()) {
        dependsOn("deployArchives")
    }
}
