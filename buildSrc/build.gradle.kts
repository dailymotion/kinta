plugins {
    id("java")
    kotlin("jvm").version("2.0.21")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.mbonnin.vespene:vespene-lib:0.5")
}
