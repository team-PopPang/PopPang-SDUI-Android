// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonElement

/** 안내·주의 배너예요. 제목과 짧은 설명을 보여 주고, 액션이 있으면 배너 전체가 눌려요. */
data class SduiNoticeBannerProps(
    /** 강조 색이에요. info는 일반 안내, warning은 꼭 알아야 할 주의예요. */
    val tone: SduiNoticeBannerTone = SduiNoticeBannerTone.Info,
    /** 배너 제목 */
    val title: String,
    /** 짧은 설명 */
    val body: String? = null,
    /** 누르면 실행해요 */
    val action: SduiAction? = null,
) {
    companion object {
        /** props를 해석 규칙대로 읽어요. 섹션을 그릴 수 없으면 SduiPropsException을 던지고, 그리되 남길 진단은 notes에 더해요. */
        fun read(props: JsonElement?, notes: MutableList<SduiPropsNote>): SduiNoticeBannerProps {
            val reader = SduiPropsReader.of(props)
            val tone = reader.enumValue("tone", required = false, fallback = SduiNoticeBannerTone.Info, lookup = SduiNoticeBannerTone::of)
            val title = reader.string("title", required = true, minLength = 1, maxLength = 30, pattern = null) ?: ""
            val body = reader.string("body", required = false, minLength = 1, maxLength = 100, pattern = null)
            val action = reader.action("action", required = false)
            reader.finish()
            notes += reader.notes
            return SduiNoticeBannerProps(tone = tone, title = title, body = body, action = action)
        }
    }
}

/** 강조 색이에요. info는 일반 안내, warning은 꼭 알아야 할 주의예요. */
enum class SduiNoticeBannerTone(val value: String) {
    Info("info"),
    Warning("warning");

    companion object {
        /** 값으로 찾아요. 모르는 값이면 null이에요. */
        fun of(value: String): SduiNoticeBannerTone? = entries.firstOrNull { it.value == value }
    }
}
