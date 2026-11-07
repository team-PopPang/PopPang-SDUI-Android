package com.poppang.sdui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.poppang.design.PPButton
import com.poppang.design.PPButtonSize
import com.poppang.design.PPButtonStyle
import com.poppang.design.PPSpacing

/**
 * button.cta: 예약하기처럼 큰 행동 버튼이에요. 모양은 Spec components/button.cta.md를 따라요.
 *
 * 모르는 액션이면 버튼은 그리고, 누르면 아무 일도 하지 않아요.
 */
@Composable
internal fun SduiButtonCtaSection(props: SduiButtonCtaProps, perform: (SduiAction) -> Unit) {
    PPButton(
        title = props.title,
        modifier = Modifier.padding(horizontal = PPSpacing.ScreenHorizontal),
        style = props.style.buttonStyle,
        size = PPButtonSize.Large,
    ) {
        perform(props.action)
    }
}

private val SduiButtonCtaStyle.buttonStyle: PPButtonStyle
    get() = when (this) {
        SduiButtonCtaStyle.Primary -> PPButtonStyle.Primary
        SduiButtonCtaStyle.Secondary -> PPButtonStyle.Secondary
    }
