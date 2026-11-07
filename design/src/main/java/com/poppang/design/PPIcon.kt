package com.poppang.design

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource

/**
 * 아이콘이에요. 이름으로 고르고, SDK에 없는 이름이면 그리지 않아요.
 *
 * 아이콘은 Spec assets/icons/의 SVG를 바꾼 VectorDrawable이에요. 꾸밈 그림이라 화면 낭독기는 읽지 않아요.
 *
 * ```kotlin
 * PPIcon("info", size = PPIconSize.Md, color = PPColor.TextSecondary)
 * ```
 *
 * @param name 아이콘 이름(예: info)
 * @param size 아이콘 크기
 * @param color 아이콘 색
 */
@Composable
fun PPIcon(
    name: String,
    modifier: Modifier = Modifier,
    size: PPIconSize = PPIconSize.Md,
    color: Color = PPColor.TextPrimary,
) {
    val drawable = PPIconCatalog.drawable(name) ?: return
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = modifier.size(size.size),
        colorFilter = ColorFilter.tint(color),
    )
}
