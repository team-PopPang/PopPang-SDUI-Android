package com.poppang.sdui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * SDUI 모듈이 앱에서 받는 값이에요. 앱 맨 위에 [SduiProvider]로 한 번 넣고, 화면마다 바꿀 수도 있어요.
 *
 * 이미지 로더와 글자 크기 방식은 디자인 모듈 환경(`PPEnvironment`)에 넣어요.
 *
 * ```kotlin
 * SduiProvider(SduiEnvironment(log = { event -> analytics.log(event) })) { MainScreen() }
 * ```
 */
@Immutable
class SduiEnvironment(
    /** SDK가 남기는 로그(노출·클릭·진단)를 받아요. 앱의 Firebase Analytics 래퍼에 이어요. */
    val log: (SduiLogEvent) -> Unit = {},
    /**
     * 섹션마다 type·id·리비전 라벨을 붙이고, fallback으로 바뀐 섹션은 빨간 테두리로 표시해요.
     * 디버그 빌드나 관리자 계정에서 켜요.
     */
    val debugOverlay: Boolean = false,
)

/** SDUI 모듈 환경 값이에요. */
val LocalSduiEnvironment = staticCompositionLocalOf { SduiEnvironment() }

/** SDUI 모듈 환경 값을 넣어요. */
@Composable
fun SduiProvider(environment: SduiEnvironment, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalSduiEnvironment provides environment, content = content)
}
