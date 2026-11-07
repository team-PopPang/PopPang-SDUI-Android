// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonElement

/** 기획전 같은 이미지 배너예요. 비율은 서버가 보내요. */
data class SduiImageBannerProps(
    /** 이미지 주소. https만 받아요. */
    val imageUrl: String,
    /** 너비 ÷ 높이예요(예: 가로로 긴 배너는 3). */
    val aspectRatio: Double,
    /** 화면 낭독기가 읽을 설명. 필수예요. */
    val accessibilityLabel: String,
    /** true면 화면 가장자리까지 그려요. */
    val fullBleed: Boolean = false,
    /** 누르면 실행해요 */
    val action: SduiAction? = null,
) {
    companion object {
        /** props를 해석 규칙대로 읽어요. 섹션을 그릴 수 없으면 SduiPropsException을 던지고, 그리되 남길 진단은 notes에 더해요. */
        fun read(props: JsonElement?, notes: MutableList<SduiPropsNote>): SduiImageBannerProps {
            val reader = SduiPropsReader.of(props)
            val imageUrl = reader.string("imageUrl", required = true, minLength = 1, maxLength = 2048, pattern = "^https://") ?: ""
            val aspectRatio = reader.number("aspectRatio", required = true, integer = false, minimum = 0.25, maximum = 4.0, exclusiveMinimum = false, exclusiveMaximum = false) ?: 0.0
            val accessibilityLabel = reader.string("accessibilityLabel", required = true, minLength = 1, maxLength = 60, pattern = null) ?: ""
            val fullBleed = reader.bool("fullBleed", required = false) ?: false
            val action = reader.action("action", required = false)
            reader.finish()
            notes += reader.notes
            return SduiImageBannerProps(imageUrl = imageUrl, aspectRatio = aspectRatio, accessibilityLabel = accessibilityLabel, fullBleed = fullBleed, action = action)
        }
    }
}
