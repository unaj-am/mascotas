pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Mascotas"
include(":app")
include(":core:database")
include(":core:data")
include(":core:ui")
include(":core:network")
include(":core:domain")
include(":features:auth")
include(":features:home")
include(":features:pets")
include(":features:walkers")
include(":features:walks")
include(":features:tracking")
include(":features:profile")
include(":features:notifications")
