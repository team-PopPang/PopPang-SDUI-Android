package com.poppang.design

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp

/**
 * 불러오는 동안 내용 자리에 두는 회색 블록이에요. iOS 앱 PPSkeletonBlock과 같은 모양이에요.
 *
 * 실제 화면 배치를 따라 블록을 놓고, 묶음 전체에 [ppShimmer]를 한 번 입혀요.
 *
 * ```kotlin
 * Column(Modifier.ppShimmer(), verticalArrangement = Arrangement.spacedBy(PPSpacing.S8)) {
 *     PPPlaceholder(height = 200.dp, radius = PPRadius.Medium)
 *     PPPlaceholder(height = 18.dp, width = 180.dp)
 * }
 * ```
 *
 * @param height 높이
 * @param width 없으면 남은 폭을 모두 써요.
 * @param radius 모서리
 */
@Composable
fun PPPlaceholder(
    height: Dp,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    radius: Dp = PPRadius.Small,
) {
    Box(
        modifier
            .then(if (width != null) Modifier.width(width) else Modifier.fillMaxWidth())
            .height(height)
            .background(PPPlaceholderSpec.Fill, RoundedCornerShape(radius)),
    )
}

/**
 * 블록 묶음 위로 밝은 빛이 왼쪽에서 오른쪽으로 지나가게 해요. iOS `ppShimmer()`와 같은 값이에요.
 *
 * 블록마다 따로 입히지 않고 묶음에 한 번 입혀서 모든 블록이 같은 박자로 빛나요.
 * 기기에서 애니메이션을 껐으면(애니메이터 길이 배율 0) 움직이지 않고 회색 블록만 보여요.
 */
fun Modifier.ppShimmer(): Modifier = composed {
    val context = LocalContext.current
    val animationsOff = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    if (animationsOff) return@composed Modifier
    val transition = rememberInfiniteTransition(label = "ppShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween((PPPlaceholderSpec.ShimmerDuration * 1000).toInt(), easing = LinearEasing)),
        label = "ppShimmerProgress",
    )
    val highlight = PPPlaceholderSpec.ShimmerHighlight.copy(alpha = PPPlaceholderSpec.ShimmerOpacity.toFloat())
    Modifier
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            // 띠가 왼쪽 밖에서 시작해 오른쪽 밖까지 지나가요. 블록이 그려진 곳에만 빛을 얹어요.
            val band = size.width * PPPlaceholderSpec.ShimmerBandRatio.toFloat()
            val start = -band + progress * (size.width + band)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, highlight, Color.Transparent),
                    startX = start,
                    endX = start + band,
                ),
                blendMode = BlendMode.SrcAtop,
            )
        }
}
