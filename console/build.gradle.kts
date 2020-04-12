plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(Libs.kotlinStdlib)

    implementation(Libs.coroutines)
    testImplementation(Libs.jUnit)
}

