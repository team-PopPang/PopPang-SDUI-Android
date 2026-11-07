package com.poppang.sdui.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poppang.design.PPColor
import com.poppang.design.PPImageRequest
import com.poppang.design.PPPalette
import com.poppang.design.PPPlaceholder
import com.poppang.design.PPRadius
import com.poppang.design.PPSpacing
import com.poppang.design.PPText
import com.poppang.design.PPType
import com.poppang.design.ppShimmer
import com.poppang.sdui.SduiAction
import com.poppang.sdui.SduiDocument
import com.poppang.sdui.SduiSections

/** 첫 출시 모양이에요. 네이티브 상세(회색 자리) 가운데 한 자리(popup_detail.addons)에 SDK가 추가 섹션을 그려요. */
@Composable
fun ShowcaseScreen(settings: DemoSettings, onAction: (SduiAction) -> Unit) {
    var document by remember { mutableStateOf(SduiDocument.decode(SHOWCASE_JSON)) }
    Column(Modifier.fillMaxSize().background(PPColor.Background)) {
        DemoTopBar(title = "팝업 상세", settings = settings) {
            TextButton(onClick = { document = SduiDocument.decode(SHOWCASE_JSON) }) { Text("다시 받기") }
        }
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(vertical = PPSpacing.S20),
            verticalArrangement = Arrangement.spacedBy(PPSpacing.S20),
        ) {
            NativePlaceholder("네이티브: 이미지·이름·운영 정보")
            document?.let { SduiSections(it, area = "popup_detail.addons", onAction = onAction) }
            NativePlaceholder("네이티브: 본문")
        }
    }
}

/** 화면 위 막대예요. 제목과 설정 버튼을 둬요. */
@Composable
fun DemoTopBar(title: String, settings: DemoSettings, leading: @Composable () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.weight(1f)) { leading() }
        Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) { DemoSettingsButton(settings) }
    }
}

/** 네이티브 화면 자리예요. 불러오는 중 모양(PPPlaceholder)으로 그려요. */
@Composable
fun NativePlaceholder(title: String) {
    Column(Modifier.padding(horizontal = PPSpacing.ScreenHorizontal), verticalArrangement = Arrangement.spacedBy(PPSpacing.S8)) {
        PPText(title, style = PPType.CaptionSm, color = PPColor.TextTertiary)
        Column(Modifier.ppShimmer(), verticalArrangement = Arrangement.spacedBy(PPSpacing.S8)) {
            PPPlaceholder(height = 120.dp, radius = PPRadius.Medium)
            PPPlaceholder(height = 18.dp, width = 180.dp)
            PPPlaceholder(height = 14.dp, width = 240.dp)
        }
    }
}

/** 서버 없이 그리는 가짜 이미지예요. 주소 마지막 이름을 적은 색 면이에요. */
@Composable
fun DemoImage(request: PPImageRequest) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(PPColor.Brand, PPPalette.Orange700))),
        contentAlignment = Alignment.Center,
    ) {
        Text(request.url.substringAfterLast('/'), color = Color.White.copy(alpha = 0.9f), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
    }
}

/** 상세 응답의 sdui 필드에 오는 문서 예시예요. 섹션 3개를 모두 써요. iOS 데모와 같은 문서예요. */
private const val SHOWCASE_JSON = """
{
  "schemaVersion": "1.0",
  "locale": "ko",
  "screen": { "id": "popup_detail", "revision": 0 },
  "sections": [
    {
      "id": "addon_reservation",
      "type": "notice.banner",
      "area": "popup_detail.addons",
      "props": {
        "tone": "warning",
        "title": "사전 예약 필수",
        "body": "주말에는 예약한 분만 입장할 수 있어요.",
        "action": { "type": "openUrl", "url": "https://example.com/reservation" }
      }
    },
    {
      "id": "addon_parking",
      "type": "notice.banner",
      "area": "popup_detail.addons",
      "props": { "title": "주차할 곳이 없어요", "body": "근처 공영 주차장을 이용해 주세요." }
    },
    {
      "id": "addon_event_banner",
      "type": "image.banner",
      "area": "popup_detail.addons",
      "props": {
        "imageUrl": "https://example.com/images/autumn-event.jpg",
        "aspectRatio": 3,
        "accessibilityLabel": "가을 기획전 배너",
        "action": { "type": "navigate", "route": "popup/0f8a2c4e-1b3d-4e5f-8a9b-0c1d2e3f4a5b" }
      }
    },
    {
      "id": "addon_reservation_button",
      "type": "button.cta",
      "area": "popup_detail.addons",
      "props": {
        "title": "예약하기",
        "action": {
          "type": "sequence",
          "actions": [
            { "type": "toast", "message": "예약 페이지로 가요" },
            { "type": "openUrl", "url": "https://example.com/reservation" }
          ]
        }
      }
    },
    {
      "id": "addon_address",
      "type": "button.cta",
      "area": "popup_detail.addons",
      "props": {
        "style": "secondary",
        "title": "주소 복사하기",
        "action": { "type": "copy", "text": "서울 성동구 연무장길 1", "toast": "주소를 복사했어요" }
      }
    }
  ]
}
"""
