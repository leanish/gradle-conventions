package io.github.leanish.gradleconventions

import java.io.File

internal fun writeFile(projectDir: File, name: String, content: String) {
    val file = projectDir.resolve(name)
    file.parentFile?.mkdirs()
    file.writeText(content)
}

internal fun writeRequiredConventionsProperties(projectDir: File) {
    writeFile(
        projectDir,
        "gradle.properties",
        "leanish.conventions.basePackage=io.github.leanish",
    )
}

internal fun environmentWithoutConventionsOverrides(
    overrides: Map<String, String> = emptyMap(),
): Map<String, String> {
    val baseEnvironment = System.getenv()
        .filterKeys { key ->
            !key.startsWith("JAVA_CONVENTIONS_") &&
                key != ConventionProperties.GITHUB_REPOSITORY_OWNER_ENV &&
                key != ConventionProperties.GITHUB_ACTOR_ENV &&
                key != ConventionProperties.GITHUB_TOKEN_ENV
        }
        .toMutableMap()
    baseEnvironment.putAll(overrides)
    return baseEnvironment
}
