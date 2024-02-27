@file:Suppress("UnstableApiUsage")
import java.net.URI

include(":core")
include(":common")
include (":app")
rootProject.name = "FileManager"


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url= URI("https://maven.google.com/")
            isAllowInsecureProtocol=true}
        maven { url= URI("https://jitpack.io")
            isAllowInsecureProtocol=true}
        jcenter()
    }
}
