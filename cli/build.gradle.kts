plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    configure(listOf(linuxX64(), macosX64())) {
        binaries {
            executable {
                baseName = "blockset"
                entryPoint("cli.main")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(rootProject)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$SERIALIZATION_VERSION")
                implementation("io.ktor:ktor-client-core:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-logging:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-json:$KTOR_VERSION")
                implementation("io.ktor:ktor-client-serialization:$KTOR_VERSION")
                implementation("com.github.ajalt.clikt:clikt:$CLIKT_VERSION")
                implementation("com.github.ajalt.mordant:mordant:$MORDANT_VERSION")
            }
        }

        val nativeCommonMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-curl:$KTOR_VERSION")
            }
        }

        val macosX64Main by getting {
            dependsOn(nativeCommonMain)
        }

        val linuxX64Main by getting {
            dependsOn(nativeCommonMain)
        }
    }
}
