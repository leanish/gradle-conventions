# Changelog

## 0.4.0

### Added
- Optional `mavenLocal()` dependency repository toggle (disabled by default):
  - `leanish.conventions.repositories.mavenLocal.enabled`
  - `JAVA_CONVENTIONS_MAVEN_LOCAL_ENABLED`
- Fine-grained publishing toggle for GitHub Packages:
  - `leanish.conventions.publishing.githubPackages.enabled`
  - `JAVA_CONVENTIONS_PUBLISHING_GITHUB_PACKAGES_ENABLED`
- Explicit fail-fast validation when a repository named `GitHubPackages` already exists but is not a Maven repository.

### Changed
- Publishing conventions no longer auto-add `mavenLocal()` as a publishing repository.
- GitHub Packages publishing is now independently toggled from overall publishing conventions.

## 0.3.1

### Changed
- Upgraded consumer-injected test dependencies:
  - `org.junit.jupiter:junit-jupiter` from `6.0.2` to `6.0.3`
  - `org.junit.platform:junit-platform-launcher` from `6.0.2` to `6.0.3`

## 0.3.0

### Breaking changes
- Renamed plugin id from `io.github.leanish.gradle-conventions` to `io.github.leanish.java-conventions`.
- Renamed project/repository from `gradle-conventions` to `java-conventions`.
- Removed compatibility alias; consumers must use the new plugin id.

### Added
- Publishing conventions:
  - automatic `maven-publish` setup (toggleable via `leanish.conventions.publishing.enabled` /
    `JAVA_CONVENTIONS_PUBLISHING_ENABLED`)
  - `mavenJava` publication defaults
  - POM license metadata (MIT)
- Generic publishing metadata resolution:
  - owner resolution via `GITHUB_REPOSITORY_OWNER`, `JAVA_CONVENTIONS_PUBLISHING_GITHUB_OWNER`,
    `leanish.conventions.publishing.githubOwner`, or `group` (`io.github.<owner>`)
  - developer id/name/url can be configured independently and are inferred from owner when missing
- Configurable repositories:
  - `mavenCentral()` can now be disabled via
    `leanish.conventions.repositories.mavenCentral.enabled` /
    `JAVA_CONVENTIONS_MAVEN_CENTRAL_ENABLED`
- NullAway base package behavior:
  - `leanish.conventions.basePackage` / `JAVA_CONVENTIONS_BASE_PACKAGE` is optional
  - when missing, base package(s) are inferred from `src/main/java` package declarations and logged
- Spotless license header support:
  - applies `LICENSE_HEADER` automatically when present
- Java artifact conventions:
  - enables `withSourcesJar()` and `withJavadocJar()`
- Test dependency conventions:
  - adds `org.junit.jupiter:junit-jupiter:6.0.2`
  - adds `org.assertj:assertj-core:3.27.7`
  - keeps JUnit Platform launcher (`org.junit.platform:junit-platform-launcher:6.0.2`) as `testRuntimeOnly`
- Plugin self-publishing support to GitHub Packages (`leanish/java-conventions`).

### Changed
- Upgraded Error Prone Gradle plugin to `5.0.0`.
- Upgraded Error Prone core/annotations to `2.47.0`.
- Refactored conventions logic into focused support classes:
  - `ConventionProperties`
  - `JavaConventionsProviders`
  - `PropertyParser`
  - `BasePackageDetector`
  - `GithubOwnerResolver`
- Replaced script-closure Checkstyle file generation with typed task:
  - `WriteCheckstyleConfigTask`

### Fixed
- Checkstyle conventions now respect consumer project files when provided:
  - `config/checkstyle/checkstyle.xml`
  - `config/checkstyle/suppressions.xml`
- Improved root git hook installation to support `.git` pointer/worktree setups.
- Expanded configuration-cache compatibility coverage and improved CI stability.
- Increased and enforced plugin test coverage with JaCoCo instruction checks.
