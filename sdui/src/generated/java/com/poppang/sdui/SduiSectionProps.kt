// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonElement

/** 섹션 type마다 읽은 props예요. 이 SDK가 아는 섹션의 등록표이기도 해요. */
sealed interface SduiSectionProps {
    /** button.cta */
    data class ButtonCta(val props: SduiButtonCtaProps) : SduiSectionProps

    /** image.banner */
    data class ImageBanner(val props: SduiImageBannerProps) : SduiSectionProps

    /** notice.banner */
    data class NoticeBanner(val props: SduiNoticeBannerProps) : SduiSectionProps

    companion object {
        /** 이 SDK가 아는 섹션 type과 처음 들어간 SDK 버전이에요. */
        val types: Map<String, String> = mapOf(
            "button.cta" to "1.0.0",
            "image.banner" to "1.0.0",
            "notice.banner" to "1.0.0",
        )

        /**
         * 아는 type이면 props를 읽어요. 모르는 type이면 null이에요(unknownType).
         * 섹션을 그릴 수 없으면 SduiPropsException을 던지고, 그리되 남길 진단은 notes에 더해요.
         */
        fun read(type: String, props: JsonElement?, notes: MutableList<SduiPropsNote>): SduiSectionProps? = when (type) {
            "button.cta" -> ButtonCta(SduiButtonCtaProps.read(props, notes))
            "image.banner" -> ImageBanner(SduiImageBannerProps.read(props, notes))
            "notice.banner" -> NoticeBanner(SduiNoticeBannerProps.read(props, notes))
            else -> null
        }
    }
}
