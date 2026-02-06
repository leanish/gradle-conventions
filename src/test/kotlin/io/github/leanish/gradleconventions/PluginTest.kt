package io.github.leanish.gradleconventions

import java.io.File
import java.nio.file.Path
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class PluginTest {
    @TempDir
    lateinit var tempDir: Path

    @Test
    fun defaultsAreApplied() {
        val projectDir = tempDir.resolve("defaults").toFile()
        projectDir.mkdirs()

        writeFile(projectDir, "settings.gradle.kts", "rootProject.name = \"test-defaults\"")
        writeFile(
            projectDir,
            "build.gradle.kts",
            """
            import org.gradle.api.tasks.compile.JavaCompile
            import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

            plugins {
                id("io.github.leanish.gradle-conventions")
            }

            tasks.register("dumpConventions") {
                doLast {
                    val compileJava = project.tasks.findByName("compileJava") as JavaCompile?
                    println("compileJavaPresent=${'$'}{compileJava != null}")
                    println("compileRelease=${'$'}{compileJava?.options?.release?.orNull}")

                    val jacoco = project.tasks.findByName("jacocoTestCoverageVerification") as JacocoCoverageVerification?
                    println("jacocoPresent=${'$'}{jacoco != null}")
                    val minimum = jacoco?.violationRules?.rules?.firstOrNull()?.limits?.firstOrNull()?.minimum
                    println("jacocoMinimum=${'$'}minimum")
                }
            }
            """.trimIndent(),
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("dumpConventions")
            .withPluginClasspath()
            .build()

        assertThat(result.output)
            .contains("compileJavaPresent=true")
            .contains("jacocoPresent=true")
            .contains("compileRelease=25")
            .contains("jacocoMinimum=0.85")
    }

    @Test
    fun overridesAreApplied() {
        val projectDir = tempDir.resolve("overrides").toFile()
        projectDir.mkdirs()

        writeFile(projectDir, "settings.gradle.kts", "rootProject.name = \"test-overrides\"")
        writeFile(
            projectDir,
            "build.gradle.kts",
            """
            import org.gradle.api.tasks.compile.JavaCompile
            import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

            plugins {
                id("io.github.leanish.gradle-conventions")
            }

            gradleConventions {
                compilerJdkVersion = 21
                bytecodeJdkVersion = 17
                runtimeJdkVersion = 21
                minimumCoverage = "0.91".toBigDecimal()
            }

            tasks.register("dumpConventions") {
                doLast {
                    val compileJava = project.tasks.findByName("compileJava") as JavaCompile?
                    println("compileJavaPresent=${'$'}{compileJava != null}")
                    println("compileRelease=${'$'}{compileJava?.options?.release?.orNull}")

                    val jacoco = project.tasks.findByName("jacocoTestCoverageVerification") as JacocoCoverageVerification?
                    println("jacocoPresent=${'$'}{jacoco != null}")
                    val minimum = jacoco?.violationRules?.rules?.firstOrNull()?.limits?.firstOrNull()?.minimum
                    println("jacocoMinimum=${'$'}minimum")
                }
            }
            """.trimIndent(),
        )

        val result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("dumpConventions")
            .withPluginClasspath()
            .build()

        assertThat(result.output)
            .contains("compileJavaPresent=true")
            .contains("jacocoPresent=true")
            .contains("compileRelease=17")
            .contains("jacocoMinimum=0.91")
    }

    @Test
    fun customPreCommitHookIsUsedWhenPresent() {
        val projectDir = tempDir.resolve("hooks").toFile()
        projectDir.mkdirs()

        writeFile(projectDir, "settings.gradle.kts", "rootProject.name = \"test-hooks\"")
        writeFile(
            projectDir,
            "build.gradle.kts",
            """
            plugins {
                id("io.github.leanish.gradle-conventions")
            }
            """.trimIndent(),
        )

        val gitHooksDir = projectDir.resolve(".git/hooks")
        gitHooksDir.mkdirs()
        val customHook = projectDir.resolve("scripts/git-hooks/pre-commit")
        customHook.parentFile.mkdirs()
        customHook.writeText("custom-hook")

        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("installGitHooks")
            .withPluginClasspath()
            .build()

        val installedHook = gitHooksDir.resolve("pre-commit")
        assertThat(installedHook.readText()).isEqualTo("custom-hook")
    }

    @Test
    fun bundledPreCommitHookIsUsedWhenNoCustomHookIsPresent() {
        val projectDir = tempDir.resolve("bundled-hooks").toFile()
        projectDir.mkdirs()

        writeFile(projectDir, "settings.gradle.kts", "rootProject.name = \"test-bundled-hooks\"")
        writeFile(
            projectDir,
            "build.gradle.kts",
            """
            plugins {
                id("io.github.leanish.gradle-conventions")
            }
            """.trimIndent(),
        )

        val gitHooksDir = projectDir.resolve(".git/hooks")
        gitHooksDir.mkdirs()

        GradleRunner.create()
            .withProjectDir(projectDir)
            .withArguments("installGitHooks")
            .withPluginClasspath()
            .build()

        val installedHook = gitHooksDir.resolve("pre-commit")
        assertThat(installedHook.readText())
            .isEqualTo(loadBundledPreCommitHook())
    }

    private fun writeFile(projectDir: File, name: String, content: String) {
        projectDir.resolve(name).writeText(content)
    }

    private fun loadBundledPreCommitHook(): String {
        val resource = requireNotNull(
            PluginTest::class.java.classLoader.getResource("git-hooks/pre-commit"),
        ) {
            "Missing bundled pre-commit hook resource"
        }
        return resource.readText()
    }
}
