package com.poppang.design

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.layout.ContentScale

/**
 * 디자인 모듈이 앱에서 받는 값이에요. 앱 맨 위에 [PPProvider]로 한 번 넣으면 아래 화면이 모두 같은 값을 써요.
 *
 * ```kotlin
 * PPProvider(
 *     PPEnvironment(
 *         image = { request ->
 *             AsyncImage( // 앱의 Coil
 *                 model = request.url,
 *                 contentDescription = null,
 *                 contentScale = request.contentScale,
 *                 modifier = Modifier.fillMaxSize(),
 *             )
 *         },
 *         textScaling = PPTextScaling.System, // 지금 AOS 동작 유지 (차이 목록 G-02)
 *     )
 * ) { MainScreen() }
 * ```
 */
@Immutable
class PPEnvironment(
    /**
     * 원격 이미지를 그리는 방법이에요. 앱의 이미지 라이브러리(Coil 등)로 받은 자리를 모두 채워요(`Modifier.fillMaxSize()`).
     * 불러오는 동안과 실패했을 때는 아무것도 그리지 않아요. 기본값은 이미지를 그리지 않아서 자리 색만 보여요.
     */
    val image: @Composable (PPImageRequest) -> Unit = {},
    /** 기기 글자 크기 설정을 따를지예요. */
    val textScaling: PPTextScaling = PPTextScaling.System,
)

/** 이미지 로더가 받는 요청이에요. */
@Immutable
data class PPImageRequest(
    /** 이미지 주소예요. */
    val url: String,
    /** 자리에 맞추는 방법이에요. */
    val contentMode: PPImageContentMode,
) {
    /** Compose의 맞추는 방법이에요. */
    val contentScale: ContentScale
        get() = when (contentMode) {
            PPImageContentMode.Fill -> ContentScale.Crop
            PPImageContentMode.Fit -> ContentScale.Fit
        }
}

/** 기기 글자 크기 설정을 따를지예요. 차이 목록 G-02예요. */
enum class PPTextScaling {
    /** 설정을 따르되 1.3배까지만 키워요. 두 앱이 맞출 동작이에요. */
    System,

    /** 설정과 상관없이 1.0배로 그려요. */
    Fixed;

    /** 기기 글자 배율(fontScale)에 맞는 글자 배율이에요. 작게 하는 설정은 그대로 따라요. */
    fun scale(fontScale: Float): Float = when (this) {
        System -> minOf(fontScale, MAXIMUM_SCALE)
        Fixed -> 1f
    }

    companion object {
        /** 글자를 키울 수 있는 가장 큰 배율이에요. */
        const val MAXIMUM_SCALE = 1.3f
    }
}

/** 디자인 모듈 환경 값이에요. */
val LocalPPEnvironment = staticCompositionLocalOf { PPEnvironment() }

/**
 * 글자 언어예요(ko·en·ja). S-Core Dream에 없는 글자(한자 등)를 이 언어의 글꼴로 채워요.
 * 없으면 기기 언어를 따라요. SDUI 섹션은 문서의 locale을 넣어요.
 */
val LocalPPTextLocale = staticCompositionLocalOf<String?> { null }

/** 디자인 모듈 환경 값을 넣어요. 앱 맨 위에 한 번 넣어요. */
@Composable
fun PPProvider(environment: PPEnvironment, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalPPEnvironment provides environment, content = content)
}
