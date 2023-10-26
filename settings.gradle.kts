pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Popular Movies"
include(":app")
include(":core:data")
include(":feature:movielist")
include(":core:designsystem")
include(":core:testing")
include(":core:network")
