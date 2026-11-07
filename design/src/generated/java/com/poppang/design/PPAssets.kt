// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 assets/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/** SDK에 들어 있는 아이콘 이름이에요. PPIcon은 이 목록에 없는 이름이면 그리지 않아요. */
object PPIconCatalog {
    val names: Set<String> = setOf(
        "chevronRight",
        "info",
        "warning",
    )

    /** 아이콘 이름에 맞는 drawable 리소스예요. 모르는 이름이면 null이에요. */
    internal fun drawable(name: String): Int? = when (name) {
        "chevronRight" -> R.drawable.pp_icon_chevron_right
        "info" -> R.drawable.pp_icon_info
        "warning" -> R.drawable.pp_icon_warning
        else -> null
    }
}

/** SDK에 들어 있는 글꼴이에요. 글꼴 역할의 굵기(fontWeight)로 파일을 골라요. */
internal object PPFontCatalog {
    /** 글꼴 이름이에요. 토큰의 fontFamily와 같아요. */
    const val FAMILY = "S-Core Dream"

    /** 굵기마다 파일이 하나씩 있는 글꼴 묶음이에요. */
    val fontFamily: FontFamily = FontFamily(
        Font(R.font.pp_scdream3, FontWeight(300)),
        Font(R.font.pp_scdream4, FontWeight(400)),
        Font(R.font.pp_scdream5, FontWeight(500)),
        Font(R.font.pp_scdream6, FontWeight(600)),
        Font(R.font.pp_scdream7, FontWeight(700)),
        Font(R.font.pp_scdream9, FontWeight(900)),
    )
}
