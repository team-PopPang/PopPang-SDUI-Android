package com.poppang.design

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * 누르면 동작하는 주요 버튼이에요. 폭은 놓인 자리를 꽉 채워요.
 *
 * primary는 주황 배경에 흰 글자, secondary는 흰 배경에 주황 글자와 테두리예요. 높이는 크기 값이 최소이고,
 * 글자가 커지면 늘어나요. 누를 때 효과는 앱 테마의 기본(Material 리플)이에요.
 *
 * ```kotlin
 * PPButton("예약하기", style = PPButtonStyle.Primary, size = PPButtonSize.Large) { perform(action) }
 * ```
 *
 * @param title 버튼 글자
 * @param style 버튼 모양
 * @param size 버튼 크기
 * @param action 누르면 실행해요
 */
@Composable
fun PPButton(
    title: String,
    modifier: Modifier = Modifier,
    style: PPButtonStyle = PPButtonStyle.Primary,
    size: PPButtonSize = PPButtonSize.Large,
    action: () -> Unit,
) {
    val shape = RoundedCornerShape(size.radius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = size.height)
            .clip(shape)
            .background(style.background)
            .then(if (style.borderWidth > 0.dp) Modifier.border(style.borderWidth, style.borderColor, shape) else Modifier)
            .clickable(role = Role.Button, onClick = action)
            .padding(horizontal = PPSpacing.S20),
        contentAlignment = Alignment.Center,
    ) {
        PPText(title, style = size.textStyle, color = style.foreground, maxLines = 1)
    }
}
