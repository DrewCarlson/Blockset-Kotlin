import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version KOTLIN_VERSION
    kotlin("plugin.serialization") version KOTLIN_VERSION
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

val testGenSrcPath = "src/commonTest/build/config"

val mavenUrl: String by ext
val mavenSnapshotUrl: String by ext

configure<PublishingExtension> {
    repositories {
        maven {
            url = if (version.toString().endsWith("SNAPSHOT")) {
                uri(mavenSnapshotUrl)
            } else {
                uri(mavenUrl)
            }
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_API_KEY")
            }
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
    watchos()
    tvos()

    targets.all(Action {
        compilations.all(Action {
            if (name.contains("test", true)) {
                compileKotlinTask.doFirst {
                    rootProject.file(testGenSrcPath).also { if (!it.exists()) it.mkdirs() }
                    val configFile = file("${testGenSrcPath}${File.separator}config.kt")
                    val bdbClientToken = System.getenv("BDB_CLIENT_TOKEN")
                    if (!configFile.exists()) {
                        checkNotNull(bdbClientToken) {
                            "BDB_CLIENT_TOKEN must be set for tests to run."
                        }
                        configFile.writeText(
                            """|package drewcarlson.blockset
                               |
                               |const val BDB_CLIENT_TOKEN = "$bdbClientToken"
                               |""".trimMargin()
                        )
                    }
                }
            }
        })
    })

    if (findProperty("hostPublishing") ?: "false" == "true") {
        val host = System.getProperty("os.name", "unknown")
        when {
            host.contains("win") -> {
                exclusivePublishing(mingwX64("win64"))
            }
            host.contains("mac") -> {
                val targets = mutableListOf<KotlinNativeTarget>(macosX64("macos"))
                ios { targets.add(this) }
                tvos { targets.add(this) }
                watchos { targets.add(this) }
                exclusivePublishing(*targets.toTypedArray())
            }
            else -> {
                exclusivePublishing(targets["metadata"], jvm(), js(BOTH), linuxX64())
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION")
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
                implementation("io.ktor:ktor-client-core-jvm:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-json-jvm:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-serialization-jvm:$KTOR_VERSION")
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
                implementation("io.ktor:ktor-client-core-js:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-json-js:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-serialization-js:$KTOR_VERSION")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-js"))
                implementation("io.ktor:ktor-client-js:$KTOR_VERSION")
                implementation(npm("node-fetch", "*"))
                implementation(npm("text-encoding", "*"))
                implementation(npm("bufferutil", "*"))
                implementation(npm("utf-8-validate", "*"))
                implementation(npm("abort-controller", "*"))
                implementation(npm("fs", "*"))
            }
        }

        val nativeCommonMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-core:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-json:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-serialization:$KTOR_VERSION")
            }
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
        val tvosMain by getting
        val tvosTest by getting
        val watchosMain by getting
        val watchosTest by getting
        configure(listOf(tvosMain, watchosMain)) {
            dependsOn(iosMain)
        }
        configure(listOf(tvosTest, watchosTest)) {
            dependsOn(iosTest)
        }
    }
}

/** Disabled all publications except for the provided [targets] */
fun exclusivePublishing(vararg targets: KotlinTarget) =
    targets.forEach { target ->
        target.mavenPublication {
            val targetPublication = this@mavenPublication
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication != targetPublication }
                .all { enabled = false }
        }
    }
