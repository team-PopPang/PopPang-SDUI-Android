// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/** 섹션을 눌렀을 때 생기는 일이에요. 이름 있는 동작만 열고, API를 부르는 범용 액션은 두지 않아요. log와 custom 액션은 이벤트·동작 이름을 정할 때 열어요. */
sealed interface SduiAction {
    /** 앱 안 화면으로 가요. */
    data class Navigate(val route: String) : SduiAction

    /** 웹 주소를 열어요. */
    data class OpenUrl(val url: String, val target: SduiOpenUrlTarget) : SduiAction

    /** 글자를 복사해요. */
    data class Copy(val text: String, val toast: String?) : SduiAction

    /** 짧은 안내를 보여 줘요. */
    data class Toast(val message: String) : SduiAction

    /** 액션 여러 개를 차례로 해요. */
    data class Sequence(val actions: List<SduiAction>) : SduiAction

    /** 이 SDK가 모르는 액션이나 route예요. 누르면 아무 일도 하지 않아요. */
    data class Unsupported(val type: String) : SduiAction

    companion object {
        /** navigate가 갈 수 있는 route 모양이에요. Spec의 schema/routes.json에서 만들었어요. */
        val routePatterns: List<Regex> = listOf(
            Regex("^popup/(?:[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\$"),
        )

        /** route를 이 SDK가 아는지 봐요. */
        fun knowsRoute(route: String): Boolean = routePatterns.any { it.containsMatchIn(route) }

        internal fun read(json: JsonElement, field: String, reader: SduiPropsReader): SduiAction {
            val type = ((json as? JsonObject)?.get("type") as? JsonPrimitive)?.takeIf { it.isString }?.content
            if (json !is JsonObject || type == null) {
                reader.markInvalid(field)
                return Unsupported("")
            }
            val child = SduiPropsReader(json, nested = true)
            val action: SduiAction = when (type) {
                "navigate" -> {
                    val route = child.string("route", required = true, minLength = 1, maxLength = null, pattern = null)
                    if (route != null && !knowsRoute(route)) {
                        child.note(SduiPropsNote.UnsupportedAction(field))
                        Unsupported(type)
                    } else {
                        Navigate(route ?: "")
                    }
                }
                "openUrl" -> OpenUrl(
                    url = child.string("url", required = true, minLength = null, maxLength = 2048, pattern = "^https://") ?: "",
                    target = child.enumValue("target", required = false, fallback = SduiOpenUrlTarget.InApp, lookup = SduiOpenUrlTarget::of),
                )
                "copy" -> Copy(
                    text = child.string("text", required = true, minLength = 1, maxLength = 200, pattern = null) ?: "",
                    toast = child.string("toast", required = false, minLength = 1, maxLength = 30, pattern = null),
                )
                "toast" -> Toast(
                    message = child.string("message", required = true, minLength = 1, maxLength = 30, pattern = null) ?: "",
                )
                "sequence" -> Sequence(
                    actions = child.actions("actions", required = true, minItems = 1, maxItems = 5) ?: emptyList(),
                )
                else -> {
                    reader.note(SduiPropsNote.UnsupportedAction(field))
                    return Unsupported(type)
                }
            }
            reader.merge(child)
            return action
        }
    }
}

/** inApp은 앱 안 브라우저, external은 다른 앱(인스타그램 등)으로 열어요. */
enum class SduiOpenUrlTarget(val value: String) {
    InApp("inApp"),
    External("external");

    companion object {
        /** 값으로 찾아요. 모르는 값이면 null이에요. */
        fun of(value: String): SduiOpenUrlTarget? = entries.firstOrNull { it.value == value }
    }
}
