# PopPang-SDUI-Android

팝팡 Android 앱이 쓰는 디자인 시스템(`design`)과 SDUI(`sdui`) SDK예요. 두 모듈은 같은 버전으로 함께 나가요.

이 레포는 팝팡 Spec 레포의 CI가 만들어 올려요. 여기서 직접 고치지 않아요.

## 받기

JitPack으로 받아요. `settings.gradle.kts`에 저장소를 더하고, 다른 라이브러리를 JitPack에서 찾지 않게 그룹을 정해 둬요.

```kotlin
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io") {
            content { includeGroup("com.github.team-PopPang.PopPang-SDUI-Android") }
        }
    }
}
```

```kotlin
dependencies {
    // design 모듈도 같이 와요.
    implementation("com.github.team-PopPang.PopPang-SDUI-Android:sdui:1.0.0")
    // 디자인 모듈만 쓸 때
    // implementation("com.github.team-PopPang.PopPang-SDUI-Android:design:1.0.0")
}
```

| 모듈 | 내용 |
| --- | --- |
| `design` | 토큰(`PPColor`·`PPType` 등), 요소(`PPText`·`PPButton`·`PPIcon`·`PPImage`·`PPPlaceholder`), 아이콘, 글꼴 |
| `sdui` | SDUI 문서 해석 엔진과 섹션 |

- minSdk 28, JVM 11이에요. AGP 8.13, Kotlin 2.3, Compose BOM 2025.12.01로 빌드해요.
- JitPack은 버전을 처음 요청받을 때 빌드해요. 처음 받을 때는 몇 분 걸릴 수 있어요.

## 폴더

| 폴더 | 내용 |
| --- | --- |
| `design/`, `sdui/` | 두 모듈 소스. `src/generated/`는 Spec 생성기가 만든 코드와 리소스예요. |
| `demo/` | 데모 앱이에요. JitPack은 데모를 빼고 두 모듈만 빌드해요(`jitpack.yml`). |

## 글꼴 저작권

S-Core Dream 글꼴(에스코어 주식회사)이 들어 있어요. 파일 내용은 고치지 않았고, Android 리소스 이름 규칙 때문에 이름만 `pp_scdream4.otf`처럼 바꿨어요. 저작권 안내는 [S-Core-Dream-License.txt](S-Core-Dream-License.txt)에 있고, 같은 안내가 `assets/pp_licenses/`로 SDK를 쓰는 앱에 같이 들어가요.

## 라이선스

이 레포의 코드에는 라이선스를 붙이지 않았어요. 팝팡 앱이 받아 쓰려고 공개한 레포예요.
