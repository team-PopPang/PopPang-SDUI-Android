package com.poppang.sdui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poppang.design.LocalPPTextLocale
import com.poppang.design.PPButton
import com.poppang.design.PPButtonSize
import com.poppang.design.PPButtonStyle
import com.poppang.design.PPColor
import com.poppang.design.PPIcon
import com.poppang.design.PPIconCatalog
import com.poppang.design.PPIconSize
import com.poppang.design.PPImage
import com.poppang.design.PPImageContentMode
import com.poppang.design.PPPlaceholder
import com.poppang.design.PPRadius
import com.poppang.design.PPSpacing
import com.poppang.design.PPText
import com.poppang.design.PPType
import com.poppang.design.ppShimmer

/** 요소 5개를 파라미터마다 그려 봐요. 두 플랫폼 스크린샷을 나란히 비교하는 화면이에요. */
@Composable
fun ElementsScreen(settings: DemoSettings) {
    Column(Modifier.fillMaxSize().background(PPColor.Background)) {
        DemoTopBar(title = "요소", settings = settings)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(horizontal = PPSpacing.ScreenHorizontal, vertical = PPSpacing.S20),
            verticalArrangement = Arrangement.spacedBy(PPSpacing.S24),
        ) {
            Group("PPText") {
                CompositionLocalProvider(LocalPPTextLocale provides "ko") {
                    PPType.entries.forEach { type -> PPText("${type.name} 가을 피크닉 팝업스토어", style = type) }
                    PPText("bodyLong 성수동에서 열리는 가을 피크닉 팝업스토어예요. 돗자리와 바구니를 빌려 드려요.", style = PPType.BodyLong)
                    PPText("maxLines 1: 아주 긴 글자는 한 줄을 넘으면 말줄임표로 줄여서 보여 줘요.", maxLines = 1)
                }
                CompositionLocalProvider(LocalPPTextLocale provides "ja") {
                    PPText("ja: 秋のピクニック ポップアップストア")
                }
            }
            Group("PPIcon") {
                Row(horizontalArrangement = Arrangement.spacedBy(PPSpacing.S15)) {
                    PPIconCatalog.names.sorted().forEach { name ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(PPSpacing.S5)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(PPSpacing.S5), verticalAlignment = Alignment.CenterVertically) {
                                PPIconSize.entries.forEach { PPIcon(name, size = it, color = PPColor.Brand) }
                            }
                            PPText(name, style = PPType.CaptionXs, color = PPColor.TextSecondary)
                        }
                    }
                }
            }
            Group("PPButton") {
                PPButtonSize.entries.forEach { size ->
                    PPButtonStyle.entries.forEach { style ->
                        PPButton("${style.name} · ${size.name}", style = style, size = size) {}
                    }
                }
            }
            Group("PPImage") {
                Row(horizontalArrangement = Arrangement.spacedBy(PPSpacing.S10)) {
                    PPImage("https://example.com/fill.jpg", Modifier.weight(1f).aspectRatio(1f))
                    PPImage("https://example.com/fit.jpg", Modifier.weight(1f).aspectRatio(1f), contentMode = PPImageContentMode.Fit)
                    PPImage("", Modifier.weight(1f).aspectRatio(1f))
                }
                PPText("마지막 칸은 주소가 없어 자리 색만 채워요.", style = PPType.CaptionSm, color = PPColor.TextSecondary)
            }
            Group("PPPlaceholder") {
                Column(Modifier.ppShimmer(), verticalArrangement = Arrangement.spacedBy(PPSpacing.S8)) {
                    PPPlaceholder(height = 160.dp, radius = PPRadius.Medium)
                    PPPlaceholder(height = 20.dp, width = 200.dp)
                    PPPlaceholder(height = 14.dp, width = 120.dp)
                }
            }
        }
    }
}

@Composable
private fun Group(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(PPSpacing.S10)) {
        Text(title, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        content()
    }
}

/** SDK가 보낸 로그예요. 앱은 같은 값을 Firebase Analytics로 보내요. */
@Composable
fun LogScreen(settings: DemoSettings) {
    Column(Modifier.fillMaxSize()) {
        DemoTopBar(title = "로그", settings = settings)
        if (settings.events.isEmpty()) {
            Text("아직 로그가 없어요. 섹션이 보이거나 섹션을 누르면 여기에 쌓여요.", Modifier.padding(16.dp), fontSize = 14.sp)
        }
        LazyColumn {
            itemsIndexed(settings.events) { _, event ->
                Text(event, Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                HorizontalDivider()
            }
        }
    }
}
