// This is the top-level Gradle settings generated for the Skip project.
// The module dependencies it contains may be symbolic links to peer folders.
//
// This file is generated by the Skip transpiler plugin and is
// derived from the aggregate Skip/skip.yml files from the SPM project.
// Edits made directly to this file will be lost.
//
// Open with External Editor to build and run this project in an IDE.
//
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
    }
    versionCatalogs {
        create("libs") {
            version("jvm", "21")
            version("android-sdk-min", "29")
            version("android-sdk-compile", "34")
            version("android-gradle-plugin", "8.2.2")
            plugin("android-library", "com.android.library").versionRef("android-gradle-plugin")
            plugin("android-application", "com.android.application").versionRef("android-gradle-plugin")
            version("kotlin", "1.9.22")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").versionRef("kotlin")
            library("kotlin-bom", "org.jetbrains.kotlin", "kotlin-bom").versionRef("kotlin")
            version("kotlin-coroutines", "1.7.3")
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").versionRef("kotlin-coroutines")
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx", "kotlinx-coroutines-android").versionRef("kotlin-coroutines")
        }
        create("testLibs") {
            version("androidx-test-runner", "1.5.2")
            library("androidx-test-runner", "androidx.test", "runner").versionRef("androidx-test-runner")
            version("androidx-test-core", "1.5.0")
            library("androidx-test-core", "androidx.test", "core").versionRef("androidx-test-core")
            version("androidx-test-rules", "1.5.0")
            library("androidx-test-rules", "androidx.test", "rules").versionRef("androidx-test-rules")
            version("androidx-test-ext-junit", "1.1.5")
            library("androidx-test-ext-junit", "androidx.test.ext", "junit").versionRef("androidx-test-ext-junit")
            version("kotlin-coroutines-test", "1.7.3")
            library("kotlinx-coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef("kotlin-coroutines-test")
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test").withoutVersion()
            library("kotlin-test-junit", "org.jetbrains.kotlin", "kotlin-test-junit").withoutVersion()
        }
    }
}

rootProject.name = "skip.unit"
include(":SkipUnit")
