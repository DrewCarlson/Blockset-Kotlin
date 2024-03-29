plugins {
    kotlin("multiplatform") version KOTLIN_VERSION
    kotlin("plugin.serialization") version KOTLIN_VERSION
    id("org.jetbrains.dokka") version "1.4.20"
}

apply(from = "gradle/publishing.gradle.kts")

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

val testGenSrcPath = "src/commonTest/build/config"

val installTestConfig by tasks.creating {
    val configFile = rootProject.file("${testGenSrcPath}/config.kt")
    onlyIf { !configFile.exists() }
    doFirst {
        rootProject.file(testGenSrcPath).also { if (!it.exists()) it.mkdirs() }
        val bdbClientToken = System.getenv("BDB_CLIENT_TOKEN")
        if (!configFile.exists()) {
            checkNotNull(bdbClientToken) {
                "BDB_CLIENT_TOKEN must be set for tests to run."
            }
            configFile.writeText(buildString {
                appendLine("package drewcarlson.blockset")
                appendLine("const val BDB_CLIENT_TOKEN = \"$bdbClientToken\"")
            })
        }
    }
}

kotlin {
    jvm()
    js(BOTH) {
        browser {
            testTask {
                useMocha {
                    timeout = "10000"
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "10000"
                }
            }
        }
    }
    macosX64("macos")
    mingwX64("win64")
    linuxX64()

    ios()
    //watchos()
    //tvos()

    targets.onEach { target ->
        target.compilations.onEach { compilation ->
            if (compilation.name.equals("test", true)) {
                compilation.compileKotlinTask.dependsOn(installTestConfig)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$SERIALIZATION_VERSION")
                implementation("io.ktor:ktor-client-core:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-json:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-serialization:$KTOR_VERSION")
            }
        }
        val commonTest by getting {
            kotlin.srcDir(testGenSrcPath)
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("io.ktor:ktor-client-okhttp:$KTOR_VERSION")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
                implementation("io.ktor:ktor-client-js:$KTOR_VERSION")
            }
        }

        val nativeCommonMain by creating {
            dependsOn(commonMain)
        }
        val nativeCommonTest by creating {
            dependsOn(commonTest)
        }
        val desktopCommonMain by creating {
            dependsOn(nativeCommonMain)
        }
        val desktopCommonTest by creating {
            dependsOn(nativeCommonTest)
            dependencies {
                implementation("io.ktor:ktor-client-curl:$KTOR_VERSION")
            }
        }

        val win64Main by getting
        val macosMain by getting
        val linuxX64Main by getting
        configure(listOf(win64Main, macosMain, linuxX64Main)) {
            dependsOn(desktopCommonMain)
        }

        val win64Test by getting
        val macosTest by getting
        val linuxX64Test by getting
        configure(listOf(win64Test, macosTest, linuxX64Test)) {
            dependsOn(desktopCommonTest)
        }

        val iosMain by getting {
            dependsOn(nativeCommonMain)
        }
        val iosTest by getting {
            dependsOn(nativeCommonTest)
            dependencies {
                implementation("io.ktor:ktor-client-ios:$KTOR_VERSION")
            }
        }

        // Configure tvos and watchos to build on ios sources
        //val tvosMain by getting
        //val tvosTest by getting
        //val watchosMain by getting
        //val watchosTest by getting
        /*configure(listOf(tvosMain, watchosMain)) {
            dependsOn(iosMain)
        }
        configure(listOf(tvosTest, watchosTest)) {
            dependsOn(iosTest)
        }*/
    }
}
