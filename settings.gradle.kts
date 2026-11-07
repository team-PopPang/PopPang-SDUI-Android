// 팝팡 Android SDK예요. 디자인 모듈(design)과 SDUI 모듈(sdui)이 같은 버전으로 함께 나가요.
// src/generated/와 테스트 픽스처는 Spec 레포의 make android가 넣어요. Spec에는 커밋하지 않고, SDK 레포에는 같이 올라가요.
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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PopPang-SDUI-Android"
include(":design", ":sdui", ":demo")
