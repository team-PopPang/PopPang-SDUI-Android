import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

// 배포 좌표는 gradle.properties에 있어요. JitPack은 com.github.team-PopPang.PopPang-SDUI-Android:sdui:버전으로 내요.
group = providers.gradleProperty("sdk.group").get()
version = providers.gradleProperty("sdk.version").get()

android {
    namespace = "com.poppang.sdui"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
    testOptions {
        unitTests {
            // 스크린샷 테스트(Robolectric)가 글꼴·아이콘 리소스를 읽어요.
            isIncludeAndroidResources = true
        }
    }
    sourceSets {
        // src/engine/: 해석 엔진. Kotlin과 kotlinx.serialization만 써요(make test-kotlin이 Android 없이 돌려요).
        // src/generated/: 섹션 props 모델·액션·등록표. make android가 넣어요.
        getByName("main") {
            java.srcDirs("src/engine/java", "src/generated/java")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    api(project(":design"))
    api(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
}

// 스크린샷 테스트는 make screenshots가 -PscreenshotDir를 줄 때만 돌려요. 다른 단위 테스트를 돌릴 때는 빼요.
tasks.withType<Test>().configureEach {
    val screenshotDir = providers.gradleProperty("screenshotDir").orNull
    if (screenshotDir != null) {
        systemProperty("poppang.screenshotDir", screenshotDir)
        systemProperty("roborazzi.test.record", "true")
    } else {
        exclude("**/SduiScreenshotTest*")
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "sdui"
            afterEvaluate { from(components["release"]) }
        }
    }
}
