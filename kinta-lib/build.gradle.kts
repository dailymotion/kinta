plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.apollographql.apollo")
}

dependencies {
    implementation(Libs.googlePlayPublisher)
    implementation(Libs.googleCloudStorage)
    implementation(Libs.googleCloudDatastore)
    implementation(Libs.jgit)
    implementation(Libs.slf4j)
    implementation(Libs.handyUriTemplates)

    implementation(Libs.coroutines)
    implementation(Libs.apollo)

    implementation(Libs.nanoHttp)

    implementation(Libs.okHttp)
    api(Libs.okHttpInterceptor)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitConverter)
    implementation(Libs.gradle)
    api(Libs.clikt)
    implementation(Libs.kotlinSerialization)
    implementation(Libs.lazySodium)
    implementation(Libs.jna)

    testImplementation(Libs.jUnit)
}

apollo {
    service("service") {
        packageName.set("com.dailymotion.kinta")
    }
}


tasks.register("kintaVersion") {
    val outputDir = file("build/generated/kotlin")

    inputs.property("version", version)
    outputs.dir(outputDir)

    doLast {
        val versionFile = file("$outputDir/com/dailymotion/kinta/Version.kt")
        versionFile.parentFile.mkdirs()
        versionFile.writeText("""// Generated file. Do not edit!
package com.dailymotion.kinta
val VERSION = "${project.version}"
""")
    }
}

(project.extensions.getByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension).sourceSets.named("main").get().kotlin.srcDir(file("build/generated/kotlin"))

tasks.getByName("compileKotlin") {
    this as org.jetbrains.kotlin.gradle.tasks.KotlinCompile
    this.source(file("build/generated/kotlin"))
    dependsOn("kintaVersion")
}

afterEvaluate {
    tasks.getByName("dokkaGfmPartial"){
        dependsOn("kintaVersion")
    }
    tasks.getByName("sourcesJar"){
        dependsOn("kintaVersion")
    }
}
