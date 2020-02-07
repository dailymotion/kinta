plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.apollographql.apollo")
}

dependencies {
    implementation(Libs.kotlinStdlib)

    implementation(Libs.googlePlayPublisher)
    implementation(Libs.googleCloudStorage)
    implementation(Libs.jgit)
    implementation(Libs.slf4j)

    implementation(Libs.coroutines)
    implementation(Libs.apollo)
    implementation(Libs.apolloCoroutinesSupport)

    implementation(Libs.nanoHttp)

    implementation(Libs.okHttp)
    implementation(Libs.okHttpInterceptor)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitConverter)
    implementation(Libs.gradle)
    api(Libs.clikt)
    implementation(Libs.kotlinSerialization)

    testImplementation(Libs.jUnit)
}

apollo {
    generateKotlinModels.set(true)
    useSemanticNaming.set(false)
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
