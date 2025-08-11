import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("multiplatform") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "org.llesha"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}
kotlin {
    jvm {
        compilations.all {

        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(false)
                }
            }
            webpackTask {
                output.libraryTarget = "commonjs2"
                outputDirectory = file("../site/js/interpreter")
            }
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.4")
                implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.github.h0tk3y.betterParse:better-parse-jvm:0.4.4")
            }
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("com.github.h0tk3y.betterParse:better-parse-js:0.4.4")
            }
        }
        val jsTest by getting
    }

}

kotlin {
    compilerOptions {
        optIn.add("kotlin.time.ExperimentalTime")
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}