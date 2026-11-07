package com.poppang.design

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density

/**
 * 글자예요. 글꼴은 역할([PPType])로 고르고, 줄 높이와 자간은 그 역할을 따라요.
 *
 * 기기 글자 크기 설정은 [PPEnvironment.textScaling]을 따라요(G-02). 1.3배를 넘는 설정은 1.3배로 그려요.
 * 글자를 담는 자리는 높이를 고정하지 않아서 글자가 커지면 세로로 늘어나요.
 *
 * ```kotlin
 * PPText("가을 피크닉 팝업스토어", style = PPType.TitleLg, maxLines = 2)
 * ```
 *
 * @param text 보여 줄 글자
 * @param style 글꼴 역할
 * @param color 글자 색
 * @param maxLines 넘치면 말줄임표로 줄여요. 없으면 줄 수를 막지 않아요.
 */
@Composable
fun PPText(
    text: String,
    modifier: Modifier = Modifier,
    style: PPType = PPType.BodyMd,
    color: Color = PPColor.TextPrimary,
    maxLines: Int? = null,
) {
    val density = LocalDensity.current
    val scale = LocalPPEnvironment.current.textScaling.scale(density.fontScale)
    val locale = LocalPPTextLocale.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, scale)) {
        BasicText(
            text = text,
            modifier = modifier,
            style = style.style.toTextStyle(color, locale),
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines ?: Int.MAX_VALUE,
        )
    }
}

/**
 * 글꼴 토큰을 Compose 글자 모양으로 바꿔요.
 *
 * - 줄 높이는 줄 하나의 높이예요(G-15). 글자를 줄 가운데에 두고 위아래를 잘라 내지 않아서, 기본 줄 높이 위에 줄 간격을
 *   위아래 반씩 두는 iOS와 줄 상자·기준선 위치가 같아요.
 * - 한국어는 어절(띄어쓰기) 단위로 줄을 바꿔요(G-16). Android 13 이상에서 동작하고, 그 아래는 글자 단위예요.
 */
internal fun PPFontStyle.toTextStyle(color: Color, locale: String?): TextStyle = TextStyle(
    color = color,
    fontFamily = if (family == PPFontCatalog.FAMILY) PPFontCatalog.fontFamily else FontFamily.Default,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
    localeList = locale?.let { LocaleList(it) },
    lineHeightStyle = LineHeightStyle(alignment = LineHeightStyle.Alignment.Center, trim = LineHeightStyle.Trim.None),
    lineBreak = LineBreak(
        strategy = LineBreak.Strategy.Simple,
        strictness = LineBreak.Strictness.Normal,
        wordBreak = LineBreak.WordBreak.Phrase,
    ),
)
