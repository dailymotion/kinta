plugins {
    id("java")
    kotlin("jvm").version("1.5.10")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.mbonnin.vespene:vespene-lib:0.5")
}
