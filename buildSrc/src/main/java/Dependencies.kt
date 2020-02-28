object Versions {
    const val coroutines = "1.3.1"
    const val kotlin = "1.3.61"
    const val apollo = "1.2.2"
    const val okhttp = "3.8.1"
    const val dokka = "0.10.0"
}

object Libs {
    const val handyUriTemplates = "com.damnhandy:handy-uri-templates:2.1.7"
    const val googlePlayPublisher = "com.google.apis:google-api-services-androidpublisher:v3-rev20191113-1.30.3"
    const val googleCloudStorage = "com.google.cloud:google-cloud-storage:1.12.0"

    const val jgit = "org.eclipse.jgit:org.eclipse.jgit:5.3.0.201903130848-r"
    const val slf4j = "org.slf4j:slf4j-simple:1.7.25"

    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val kotlinSerialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.0"

    const val apollo = "com.apollographql.apollo:apollo-runtime:${Versions.apollo}"
    const val apolloCoroutinesSupport = "com.apollographql.apollo:apollo-coroutines-support:${Versions.apollo}"

    const val nanoHttp = "org.nanohttpd:nanohttpd:2.2.0"

    const val okHttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okHttpInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    const val retrofit = "com.squareup.retrofit2:retrofit:2.4.0"
    const val retrofitConverter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.4.0"

    const val gradle = "org.gradle:gradle-tooling-api:6.0.1"

    const val clikt = "com.github.ajalt:clikt:2.4.0"

    const val jUnit = "junit:junit:4.12"
}
