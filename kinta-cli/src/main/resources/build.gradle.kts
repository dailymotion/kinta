plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.61")
}

repositories {
    jcenter()
    mavenLocal()
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
    }
}

configure<JavaPluginExtension> {
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api("com.dailymotion.kinta:kinta-lib:{KINTA_VERSION}")
    // The builtin workflows are in [com.dailymotion.kinta.BuiltinWorkflows].
    // Comment this line if you don't want the builtin kinta workflows
    implementation("com.dailymotion.kinta:kinta-workflows:{KINTA_VERSION}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.withType<Jar> {

    archiveFileName.set("kinta-workflows-custom.jar")
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        /**
         * we remove INDEX.LIST else java does not find the MainClass
         * since we remove INDEX.LIST, it also looks like we need to remove signatures too. Not 100% sure why
         */
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/INDEX.LIST", "META-INF/*.kotlin_module")
    }
}
