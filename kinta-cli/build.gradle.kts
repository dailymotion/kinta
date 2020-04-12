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
    api(Libs.slf4j)

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
    implementation(project(":kinta-workflows"))
    testImplementation(Libs.jUnit)
}

application {
    mainClassName = "com.dailymotion.kinta.MainKt"
    applicationName = "kinta"
}