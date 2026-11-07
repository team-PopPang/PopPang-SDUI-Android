package com.poppang.sdui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import com.poppang.design.PPColor
import com.poppang.design.PPIcon
import com.poppang.design.PPIconSize
import com.poppang.design.PPRadius
import com.poppang.design.PPSpacing
import com.poppang.design.PPText
import com.poppang.design.PPType

/**
 * notice.banner: 안내·주의 배너예요. 모양은 Spec components/notice.banner.md를 따라요.
 *
 * 액션이 있으면 배너 전체가 버튼이 되고 오른쪽에 화살표를 보여요. 모르는 액션만 있으면 액션이 없는 배너로 그려요.
 */
@Composable
internal fun SduiNoticeBannerSection(props: SduiNoticeBannerProps, perform: (SduiAction) -> Unit) {
    val action = props.action?.takeIf { it.steps.isNotEmpty() }
    Row(
        modifier = Modifier
            .padding(horizontal = PPSpacing.ScreenHorizontal)
            .fillMaxWidth()
            .clip(RoundedCornerShape(PPRadius.Medium))
            .background(props.tone.background)
            .then(
                if (action != null) {
                    Modifier.clickable(role = Role.Button) { perform(action) }
                } else {
                    Modifier.semantics(mergeDescendants = true) {}
                },
            )
            .padding(PPSpacing.S15),
        horizontalArrangement = Arrangement.spacedBy(PPSpacing.S10),
        verticalAlignment = Alignment.Top,
    ) {
        PPIcon(props.tone.icon, size = PPIconSize.Md, color = props.tone.iconColor)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PPSpacing.S4)) {
            PPText(props.title, style = PPType.LabelMd, color = PPColor.TextPrimary)
            props.body?.let { PPText(it, style = PPType.CaptionSm, color = PPColor.TextSecondary) }
        }
        if (action != null) {
            PPIcon("chevronRight", size = PPIconSize.Md, color = PPColor.TextTertiary)
        }
    }
}

private val SduiNoticeBannerTone.background: Color
    get() = when (this) {
        SduiNoticeBannerTone.Info -> PPColor.BackgroundSecondary
        SduiNoticeBannerTone.Warning -> PPColor.BrandSubtle
    }

private val SduiNoticeBannerTone.icon: String
    get() = when (this) {
        SduiNoticeBannerTone.Info -> "info"
        SduiNoticeBannerTone.Warning -> "warning"
    }

private val SduiNoticeBannerTone.iconColor: Color
    get() = when (this) {
        SduiNoticeBannerTone.Info -> PPColor.TextSecondary
        SduiNoticeBannerTone.Warning -> PPColor.Brand
    }
