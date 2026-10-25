// FILE INFORMATION: settings.gradle.kts
// Project: AI Phone Assistant
// Developer/Maintainer: Dipak Choudhary | Powered By: KAIxGenShopLabs
// CREATOR SIGNATURE: Dipak Choudhary | SECTION START: GRADLE SETTINGS
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
rootProject.name = "AI-Phone-Assistant"
include(":app")
// SECTION END: GRADLE SETTINGS
// CREATOR SIGNATURE: Dipak Choudhary | KAIxGenShopLabs
