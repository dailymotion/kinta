plugins {
    application
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Libs.kotlinStdlib)

    implementation(Libs.googlePlayPublisher)
    implementation(Libs.googleCloudStorage)
    implementation(Libs.jgit)
    // had to add SLF4J: upgrading JGIT results in "SLF4J: Failed to load class org.slf4j.impl.StaticLoggerBinder"
    implementation(Libs.slf4j)

    implementation(Libs.coroutines)

    implementation(Libs.nanoHttp)

    implementation(Libs.okHttp)
    implementation(Libs.okHttpInterceptor)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitConverter)
    implementation(Libs.gradle)
    implementation(Libs.clikt)
    implementation(Libs.kotlinSerialization)
    implementation(project(":kinta-lib"))

    testImplementation(Libs.jUnit)
}

tasks.withType<Jar> {
    archiveFileName.set("kinta-cli.jar")
    manifest {
        attributes("Main-Class" to "com.dailymotion.kinta.MainKt")
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        /**
         * we remove INDEX.LIST else java does not find the MainClass
         * since we remove INDEX.LIST, it also looks like we need to remove signatures too. Not 100% sure why
         */
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/INDEX.LIST", "META-INF/*.kotlin_module")
    }
}

application {
    mainClassName = "com.dailymotion.kinta.MainKt"
}