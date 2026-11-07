package com.poppang.design

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

/**
 * 주소로 불러오는 이미지예요. 불러오는 동안과 실패했을 때는 자리 색을 채워요.
 *
 * 크기는 놓는 곳이 정하고(예: `Modifier.fillMaxWidth().aspectRatio(3f)`), 이미지는 받은 자리를 모두 써요.
 * 불러오기와 캐시는 [PPEnvironment.image](앱의 이미지 로더)가 해요.
 *
 * @param url 이미지 주소
 * @param contentMode 자리에 맞추는 방법
 * @param radius 모서리. 카드·이미지는 medium이에요.
 * @param placeholderColor 불러오기 전과 실패했을 때 채우는 색
 */
@Composable
fun PPImage(
    url: String,
    modifier: Modifier = Modifier,
    contentMode: PPImageContentMode = PPImageContentMode.Fill,
    radius: Dp = PPRadius.Medium,
    placeholderColor: Color = PPColor.BackgroundSecondary,
) {
    val image = LocalPPEnvironment.current.image
    Box(modifier.clip(RoundedCornerShape(radius)).background(placeholderColor)) {
        if (url.isNotEmpty()) {
            Box(Modifier.matchParentSize()) {
                image(PPImageRequest(url, contentMode))
            }
        }
    }
}
