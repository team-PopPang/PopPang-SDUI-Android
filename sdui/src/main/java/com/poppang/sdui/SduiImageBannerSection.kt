package com.poppang.sdui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.poppang.design.PPImage
import com.poppang.design.PPImageContentMode
import com.poppang.design.PPRadius
import com.poppang.design.PPSpacing

/**
 * image.banner: 기획전 같은 이미지 배너예요. 모양은 Spec components/image.banner.md를 따라요.
 *
 * 폭은 꽉 채우고 높이는 폭 ÷ aspectRatio예요. fullBleed면 가장자리까지 그리고 모서리는 0이에요.
 * 액션이 있으면 배너 전체가 버튼이에요. 모르는 액션만 있으면 액션이 없는 배너로 그려요.
 */
@Composable
internal fun SduiImageBannerSection(props: SduiImageBannerProps, perform: (SduiAction) -> Unit) {
    val action = props.action?.takeIf { it.steps.isNotEmpty() }
    val label = props.accessibilityLabel
    PPImage(
        url = props.imageUrl,
        modifier = Modifier
            .padding(horizontal = if (props.fullBleed) 0.dp else PPSpacing.ScreenHorizontal)
            .fillMaxWidth()
            .aspectRatio(props.aspectRatio.toFloat())
            .then(
                if (action != null) {
                    Modifier
                        .clickable(role = Role.Button) { perform(action) }
                        .semantics { contentDescription = label }
                } else {
                    Modifier.semantics {
                        contentDescription = label
                        role = Role.Image
                    }
                },
            ),
        contentMode = PPImageContentMode.Fill,
        radius = if (props.fullBleed) 0.dp else PPRadius.Medium,
    )
}
