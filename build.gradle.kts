plugins {
    `maven-publish`
    alias(libs.plugins.android.library)

    alias(libs.plugins.spotless)
}

group = "io.github.eutera.trackline"

version = "0.1.0"

kotlin { explicitApi() }

android {
    publishing { singleVariant("release") { withSourcesJar() } }

    namespace = "io.github.eutera.trackline.provider.api"
    compileSdk { version = release(37) }
    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        warningsAsErrors = true
        informational += "AndroidGradlePluginVersion"
    }
}

abstract class GenerateLicenseAsset : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val licenseFile: RegularFileProperty
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val asset = outputDirectory.file("licenses/PROVIDER_API-LICENSE").get().asFile
        licenseFile.get().asFile.copyTo(asset, overwrite = true)
    }
}

val providerApiLicense = layout.projectDirectory.file("LICENSE")
val generateLicenseAsset =
    tasks.register<GenerateLicenseAsset>("generateLicenseAsset") {
        licenseFile.set(providerApiLicense)
    }

androidComponents.onVariants { variant ->
    variant.sources.assets?.addGeneratedSourceDirectory(
        generateLicenseAsset,
        GenerateLicenseAsset::outputDirectory,
    )
}

tasks.withType<Jar>().configureEach {
    if (name == "sourceReleaseJar") {
        from(providerApiLicense) { into("META-INF") }
    }
}

dependencies { api(libs.kotlinx.coroutines.core) }

spotless {
    kotlin {
        target("src/**/*.kt")
        ktfmt("0.61").kotlinlangStyle()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktfmt("0.61").kotlinlangStyle()
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            afterEvaluate { from(components["release"]) }
            pom {
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
            }
        }
    }
}

publishing.publications.withType<MavenPublication>().configureEach {
    providers.gradleProperty("sourceRepositoryUrl").orNull?.let { sourceUrl ->
        pom {
            url.set(sourceUrl)
            scm {
                url.set(sourceUrl)
                connection.set("scm:git:$sourceUrl.git")
            }
        }
    }
}
