plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(Libs.kotlinStdlib)

    implementation(Libs.jgit)
    implementation(Libs.slf4j)
    implementation(Libs.coroutines)
    implementation(Libs.okHttp)
    implementation(Libs.okHttpInterceptor)
    implementation(Libs.retrofit)
    implementation(Libs.retrofitConverter)
    implementation(project(":kinta-lib"))
    implementation(project(":console"))
    implementation(Libs.clikt)
    implementation(Libs.kotlinSerialization)

    implementation(Libs.jgit)
    implementation(Libs.googleCloudStorage)

    testImplementation(Libs.jUnit)
}

