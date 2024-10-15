import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version(Versions.kotlin).apply(false)
    id("org.jetbrains.kotlin.plugin.serialization").version(Versions.kotlin).apply(false)
    id("com.apollographql.apollo").version(Versions.apollo).apply(false)
    id("org.jetbrains.dokka").version(Versions.dokka).apply(false)
}

version = "0.1.17-SNAPSHOT"

repositories {
    mavenCentral()
}

apply(plugin = "org.jetbrains.dokka")

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask> {
    outputDirectory.set(rootDir.resolve("build/kdoc"))
}

subprojects {
    repositories {
        mavenCentral()
        maven ("https://repo.gradle.org/gradle/libs-releases/")
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            allWarningsAsErrors = true
        }
    }

    afterEvaluate {
        this.configure<JavaPluginExtension> {
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    group = "com.dailymotion.kinta"
    version = rootProject.version

    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial> {
        dokkaSourceSets {
            configureEach {
                reportUndocumented.set(false)
            }
        }
    }

    afterEvaluate {
        configureMavenPublish()
    }

    tasks.register<Task>("deployArtifactsIfNeeded") {
        if (isTag()) {
            project.logger.lifecycle("Upload to OSSStaging needed.")
            dependsOn("publishDefaultPublicationToOssStagingRepository")
        } else if (isPushMaster()) {
            project.logger.lifecycle("Upload to OSSSnapshots needed.")
            dependsOn("publishDefaultPublicationToOssSnapshotsRepository")
        }
    }
}

fun Project.getOssStagingUrl(): String {
    val url = try {
        this.extensions.extraProperties["ossStagingUrl"] as String?
    } catch (e: ExtraPropertiesExtension.UnknownPropertyException) {
        null
    }
    if (url != null) {
        return url
    }
    val client = net.mbonnin.vespene.lib.NexusStagingClient(
        baseUrl = "https://s01.oss.sonatype.org/service/local/",
        username = System.getenv("SONATYPE_NEXUS_USERNAME"),
        password = System.getenv("SONATYPE_NEXUS_PASSWORD")
    )
    val repositoryId = runBlocking {
        client.createRepository(
            profileId = System.getenv("KINTA_STAGING_PROFILE_ID"),
            description = "$group $name $version"
        )
    }
    println("publishing to '$repositoryId")
    return "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${repositoryId}/".also {
        this.extensions.extraProperties["ossStagingUrl"] = it
    }
}

fun isTag(): Boolean {
    val ref = System.getenv("GITHUB_REF")
    return ref?.startsWith("refs/tags/") == true
}

fun isPushMaster(): Boolean {
    val eventName = System.getenv("GITHUB_EVENT_NAME")
    val ref = System.getenv("GITHUB_REF")

    return eventName == "push" && (ref == "refs/heads/master")
}

tasks.register("deployDocsIfNeeded") {
    if (isPushMaster()) {
        dependsOn("deployDocs")
    }
}

tasks.register("deployArchivesIfNeeded") {
    if (isTag()) {
        dependsOn("deployArchives")
    }
}

fun Project.configureMavenPublish() {
    val javaPluginConvention = project.convention.findPlugin(JavaPluginConvention::class.java)
    val sourcesJarTaskProvider = tasks.register("sourcesJar", org.gradle.jvm.tasks.Jar::class.java) {
        archiveClassifier.set("sources")
        from(javaPluginConvention?.sourceSets?.get("main")?.allSource)
    }

    val javadocJarTaskProvider = tasks.register("docJar", org.gradle.jvm.tasks.Jar::class.java) {
        archiveClassifier.set("javadoc")
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("default") {
                from(components.findByName("java"))

                artifact(sourcesJarTaskProvider.get())
                artifact(javadocJarTaskProvider.get())

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
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/dailymotion/kinta/blob/master/LICENSE")
                        }
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
                name = "ossStaging"
                setUrl {
                    uri(rootProject.getOssStagingUrl())
                }
                credentials {
                    username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }
            maven {
                name = "ossSnapshots"
                url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                credentials {
                    username = System.getenv("SONATYPE_NEXUS_USERNAME")
                    password = System.getenv("SONATYPE_NEXUS_PASSWORD")
                }
            }
        }
    }

    configure<SigningExtension> {
        // GPG_PRIVATE_KEY should contain the armoured private key that starts with -----BEGIN PGP PRIVATE KEY BLOCK-----
        // It can be obtained with gpg --armour --export-secret-keys KEY_ID
        useInMemoryPgpKeys(System.getenv("KINTA_GPG_PRIVATE_KEY"), System.getenv("KINTA_GPG_PRIVATE_KEY_PASSWORD"))
        val publicationsContainer = (extensions.getByName("publishing") as PublishingExtension).publications
        sign(publicationsContainer)
    }
    tasks.withType(Sign::class.java).configureEach {
        isEnabled = !System.getenv("KINTA_GPG_PRIVATE_KEY").isNullOrBlank()
    }
}

apply(from = "docs.gradle.kts")
