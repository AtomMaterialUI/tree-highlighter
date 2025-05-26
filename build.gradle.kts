import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.kotlin.dsl.changelog
import org.gradle.kotlin.dsl.withType
import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun properties(key: String) = providers.gradleProperty(key).get()
fun fileProperties(key: String) = project.findProperty(key).toString().let { if (it.isNotEmpty()) file(it) else null }
fun environment(key: String) = providers.environmentVariable(key)


plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.gradleIntelliJPlugin)
  alias(libs.plugins.changelog)
  alias(libs.plugins.detekt)
  alias(libs.plugins.ktlint)
}

// Import variables from gradle.properties file
val pluginGroup: String by project
val pluginName: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

val javaVersion: String by project

group = pluginGroup
version = pluginVersion


// Set the JVM language level used to build the project.
kotlin {
  jvmToolchain(21)
}

// Configure project's dependencies
repositories {
  mavenCentral()
  mavenLocal()
  gradlePluginPortal()

  intellijPlatform {
    defaultRepositories()
    jetbrainsRuntime()
  }
}

// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")
  implementation("commons-io:commons-io:2.19.0")
  implementation("com.thoughtworks.xstream:xstream:1.4.21")

  intellijPlatform {
    intellijIdeaUltimate(platformVersion, useInstaller = false)
    pluginVerifier()
    zipSigner()

    bundledPlugins(
      "com.intellij.java",
    )
  }
}

// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
  pluginConfiguration {
    id = pluginGroup
    name = pluginName
    version = pluginVersion

    ideaVersion {
      sinceBuild = pluginSinceBuild
      untilBuild = pluginUntilBuild
    }

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
    description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
      val start = "<!-- Plugin description -->"
      val end = "<!-- Plugin description end -->"

      with(it.lines()) {
        if (!containsAll(listOf(start, end))) {
          throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
        }
        subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
      }
    }

    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    changeNotes = provider {
      with(changelog) {
        renderItem(
          (getOrNull(pluginVersion) ?: getUnreleased())
            .withHeader(false)
            .withEmptySections(false),
          Changelog.OutputType.HTML,
        )
      }
    }
  }

  signing {
    certificateChain = environment("CERTIFICATE_CHAIN")
    privateKey = environment("PRIVATE_KEY")
    password = environment("PRIVATE_KEY_PASSWORD")
  }

  publishing {
    token = environment("PUBLISH_TOKEN")
    channels = listOf(pluginVersion.split('-').getOrElse(1) { "default" }.split('.').first())
  }
}

changelog {
  path.set("${project.projectDir}/docs/CHANGELOG.md")
  version.set(pluginVersion)
  itemPrefix.set("-")
  keepUnreleasedSection.set(true)
  unreleasedTerm.set("Changelog")
  groups.set(listOf("Features", "Fixes", "Other", "Bump"))
}

detekt {
  config.setFrom("./detekt-config.yml")
  buildUponDefaultConfig = true
  autoCorrect = true
}

tasks {
  javaVersion.let {
    // Set the compatibility versions to 1.8
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }

    withType<Detekt> {
      jvmTarget = it
      reports.xml.required.set(true)
    }
  }

  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  buildSearchableOptions {
    enabled = false
  }

  register("markdownToHtml") {
    val input = File("./docs/CHANGELOG.md")
    File("./docs/CHANGELOG.html").run {
      writeText(markdownToHTML(input.readText()))
    }
  }
}