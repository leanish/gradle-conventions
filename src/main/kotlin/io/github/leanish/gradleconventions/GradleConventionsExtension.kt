package io.github.leanish.gradleconventions

import java.math.BigDecimal
import javax.inject.Inject
import org.gradle.api.Action
import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.jvm.toolchain.JvmVendorSpec

private val defaultJavaExecJvmArgs = emptyList<String>()
private val defaultTestJvmArgs = emptyList<String>()
private const val defaultJdkVersion = 25
private val defaultJdkVendor = JvmVendorSpec.ADOPTIUM
private val defaultSpotlessConfig = Action<SpotlessExtension> {
    java {
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
private val defaultErrorproneArgs = listOf(
    "-Xep:NullAway:ERROR",
    "-XepOpt:NullAway:AnnotatedPackages=io.github.leanish",
    "-Xep:FutureReturnValueIgnored:OFF",
)
private val defaultMinimumCoverage = "0.85".toBigDecimal()

/**
 * Values to be mutated to parameterize the plugin.
 * Important: apply these changes during Gradle configuration (e.g., inside build.gradle.kts), changes made later in task actions will not be picked up.
 */
open class GradleConventionsExtension @Inject constructor() {
    // Extra JVM args for JavaExec tasks (bootRun, etc.).
    val javaExecJvmArgs: MutableList<String> = defaultJavaExecJvmArgs.toMutableList()

    // Extra JVM args for Test tasks.
    val testJvmArgs: MutableList<String> = defaultTestJvmArgs.toMutableList()

    // Compiler JDK version used by JavaCompile tasks and annotation processors.
    var compilerJdkVersion: Int = defaultJdkVersion

    // Bytecode level used for javac --release.
    var bytecodeJdkVersion: Int = defaultJdkVersion

    // JDK version used to run tests and JavaExec tasks.
    var runtimeJdkVersion: Int = defaultJdkVersion

    // Convenience setter that keeps compiler/bytecode/runtime aligned.
    @Suppress("unused")
    var jdkVersion: Int
        get() = compilerJdkVersion
        set(value) {
            compilerJdkVersion = value
            bytecodeJdkVersion = value
            runtimeJdkVersion = value
        }

    // Vendor used by JavaCompile tasks and annotation processors.
    var compilerJdkVendor: JvmVendorSpec = defaultJdkVendor

    // Vendor used to run tests and JavaExec tasks.
    var runtimeJdkVendor: JvmVendorSpec = defaultJdkVendor

    // Convenience setter that keeps compiler/runtime vendors aligned.
    @Suppress("unused")
    var jdkVendor: JvmVendorSpec
        get() = compilerJdkVendor
        set(value) {
            compilerJdkVendor = value
            runtimeJdkVendor = value
        }

    // Spotless root config hook (defaults to basic Java hygiene steps; override to replace).
    var spotlessConfig: Action<SpotlessExtension> = defaultSpotlessConfig

    // ErrorProne/NullAway args; defaults are pre-populated.
    val errorproneArgs: MutableList<String> = defaultErrorproneArgs.toMutableList()

    // Jacoco minimum instruction coverage ratio.
    var minimumCoverage: BigDecimal = defaultMinimumCoverage
}
