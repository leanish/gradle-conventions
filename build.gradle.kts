plugins {
    `kotlin-dsl`
    `maven-publish`
    id("com.gradle.plugin-publish") version "2.0.0"
}

group = "io.github.leanish"
version = "0.3.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java {
    toolchain {
        // Build/test the plugin on Java 25 to mirror the conventions' default toolchain.
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    // Gradle embeds Kotlin 2.2.x today; pinning it to JDK 21 until Gradle uses Kotlin 2.3+.
    jvmToolchain(21)
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.2.1")
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:4.4.0")
    testImplementation(gradleTestKit())
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    website.set("https://github.com/leanish/java-conventions")
    vcsUrl.set("https://github.com/leanish/java-conventions")
    plugins {
        val pluginTags = listOf(
            "conventions",
            "java",
            "checkstyle",
            "spotless",
            "junit",
            "coverage",
            "jacoco",
            "errorprone",
            "nullaway",
            "license",
            "git-hooks",
            "publishing",
            "maven-publish",
        )

        named("io.github.leanish.java-conventions") {
            displayName = "Leanish Java Conventions"
            description = "Shared Gradle conventions for Java projects."
            tags.set(pluginTags)
        }
    }
}
