import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

// 배포 좌표는 gradle.properties에 있어요. JitPack은 com.github.team-PopPang.PopPang-SDUI-Android:design:버전으로 내요.
group = providers.gradleProperty("sdk.group").get()
version = providers.gradleProperty("sdk.version").get()

android {
    namespace = "com.poppang.design"
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
    sourceSets {
        // 토큰·요소 enum·아이콘·글꼴·글꼴 저작권 안내는 make android가 src/generated/에 넣어요.
        getByName("main") {
            java.srcDir("src/generated/java")
            res.srcDir("src/generated/res")
            assets.srcDir("src/generated/assets")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.foundation)
    testImplementation(libs.junit)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "design"
            afterEvaluate { from(components["release"]) }
        }
    }
}
