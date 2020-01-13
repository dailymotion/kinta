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