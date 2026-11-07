// src/engine/ 파일은 Kotlin과 kotlinx.serialization만 써요. make test-kotlin이 Android 없이 이 파일로 픽스처를 돌려요.
package com.poppang.sdui

/** SDK 정보예요. 로그와 요청 헤더(X-SDUI-SDK-Version, X-Platform)에 써요. */
object SduiSdk {
    /** SDK 버전이에요. 배포할 때 Spec 태그와 같게 맞춰요. */
    const val VERSION = "1.0.0"

    /** 플랫폼 이름이에요. */
    const val PLATFORM = "android"
}

/**
 * SDK가 남기는 로그예요. 앱은 `SduiEnvironment.log`로 받아 Firebase Analytics로 보내요.
 *
 * 이벤트 이름은 0단계 결정 11번으로 고정했어요. 모든 이벤트에 screen_id, revision, section_id, section_type,
 * position(섹션 자리, 1부터), sdk_version, platform이 붙고, 섹션의 logging 값도 같이 붙어요.
 * 파라미터 값은 String, Long, Double, Boolean이에요.
 */
sealed interface SduiLogEvent {
    /** Firebase Analytics 이벤트 이름이에요. 진단은 정한 이름이 없어서 null이에요. */
    val name: String?

    /** 이벤트 파라미터예요. */
    val parameters: Map<String, Any>

    /** 섹션이 절반 넘게 0.5초 보였어요. 문서를 받을 때마다 섹션마다 한 번이에요. */
    data class SectionView(override val parameters: Map<String, Any>) : SduiLogEvent {
        override val name: String get() = SduiLogEvent.SECTION_VIEW
    }

    /** 섹션의 액션을 실행했어요. action_type이 더 붙어요. */
    data class Action(val action: SduiAction, override val parameters: Map<String, Any>) : SduiLogEvent {
        override val name: String get() = SduiLogEvent.ACTION
    }

    /**
     * 문서를 읽다가 생긴 진단이에요. 문서를 받을 때마다 자리마다 한 번이에요.
     * 이벤트 이름은 아직 정하지 않아서 앱이 보낼 곳을 정해요. code, field가 더 붙어요.
     */
    data class Diagnostic(val diagnostic: SduiDiagnostic, override val parameters: Map<String, Any>) : SduiLogEvent {
        override val name: String? get() = null
    }

    companion object {
        /** 노출 이벤트 이름 */
        const val SECTION_VIEW = "sdui_section_view"

        /** 클릭 이벤트 이름 */
        const val ACTION = "sdui_action"
    }
}

/** 섹션 로그에 붙는 값이에요. 섹션의 logging 값 위에 SDK 값을 덮어써요. [position]은 그 자리에서 몇 번째 섹션인지예요(1부터). */
fun SduiDocument.logParameters(section: SduiSection, position: Int): Map<String, Any> {
    val parameters = LinkedHashMap(section.logging)
    parameters["section_id"] = section.id
    parameters["section_type"] = section.type
    parameters["position"] = position.toLong()
    parameters.putAll(commonLogParameters())
    return parameters
}

/** 진단 로그에 붙는 값이에요. */
fun SduiDocument.logParameters(diagnostic: SduiDiagnostic): Map<String, Any> {
    val parameters = LinkedHashMap<String, Any>()
    parameters["code"] = diagnostic.code.value
    diagnostic.sectionId?.let { parameters["section_id"] = it }
    diagnostic.sectionType?.let { parameters["section_type"] = it }
    diagnostic.field?.let { parameters["field"] = it }
    parameters.putAll(commonLogParameters())
    return parameters
}

private fun SduiDocument.commonLogParameters(): Map<String, Any> = mapOf(
    "screen_id" to (screen?.id ?: ""),
    "revision" to (screen?.revision ?: 0).toLong(),
    "sdk_version" to SduiSdk.VERSION,
    "platform" to SduiSdk.PLATFORM,
)

/** 앱에 넘길 차례대로 펼친 액션이에요. sequence는 안의 액션을 펼치고, 모르는 액션은 빼요. */
val SduiAction.steps: List<SduiAction>
    get() = when (this) {
        is SduiAction.Sequence -> actions.flatMap { it.steps }
        is SduiAction.Unsupported -> emptyList()
        else -> listOf(this)
    }

/** 액션 이름(navigate 등)이에요. 로그의 action_type이에요. */
val SduiAction.typeName: String
    get() = when (this) {
        is SduiAction.Navigate -> "navigate"
        is SduiAction.OpenUrl -> "openUrl"
        is SduiAction.Copy -> "copy"
        is SduiAction.Toast -> "toast"
        is SduiAction.Sequence -> "sequence"
        is SduiAction.Unsupported -> type
    }
