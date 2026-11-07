// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonElement

/** 예약하기처럼 큰 행동 버튼이에요. 화면 폭을 꽉 채우고 PPButton large로 그려요. */
data class SduiButtonCtaProps(
    /** 버튼 모양이에요. PPButtonStyle과 같아요. */
    val style: SduiButtonCtaStyle = SduiButtonCtaStyle.Primary,
    /** 버튼 글자 */
    val title: String,
    /** 누르면 실행해요 */
    val action: SduiAction,
) {
    companion object {
        /** props를 해석 규칙대로 읽어요. 섹션을 그릴 수 없으면 SduiPropsException을 던지고, 그리되 남길 진단은 notes에 더해요. */
        fun read(props: JsonElement?, notes: MutableList<SduiPropsNote>): SduiButtonCtaProps {
            val reader = SduiPropsReader.of(props)
            val style = reader.enumValue("style", required = false, fallback = SduiButtonCtaStyle.Primary, lookup = SduiButtonCtaStyle::of)
            val title = reader.string("title", required = true, minLength = 1, maxLength = 20, pattern = null) ?: ""
            val action = reader.action("action", required = true) ?: SduiAction.Unsupported("")
            reader.finish()
            notes += reader.notes
            return SduiButtonCtaProps(style = style, title = title, action = action)
        }
    }
}

/** 버튼 모양이에요. PPButtonStyle과 같아요. */
enum class SduiButtonCtaStyle(val value: String) {
    Primary("primary"),
    Secondary("secondary");

    companion object {
        /** 값으로 찾아요. 모르는 값이면 null이에요. */
        fun of(value: String): SduiButtonCtaStyle? = entries.firstOrNull { it.value == value }
    }
}
