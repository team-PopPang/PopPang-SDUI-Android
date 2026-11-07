// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 요소 모양: PPButton(title: String, style: PPButtonStyle = PPButtonStyle.Primary, size: PPButtonSize = PPButtonSize.Large, action: () -> Unit)

/** 버튼 모양이에요. primary는 iOS MainOrangeButton·AOS CustomButton, secondary는 iOS isReversed·AOS CustomButton3 모양이에요. */
enum class PPButtonStyle(
    val background: Color,
    val foreground: Color,
    val borderColor: Color,
    val borderWidth: Dp,
) {
    /** 주황 배경에 흰 글자(G-05) */
    Primary(PPColor.Brand, PPColor.TextOnBrand, PPColor.Brand, 0.dp),
    /** 흰 배경에 주황 글자와 1 두께 테두리 */
    Secondary(PPColor.Background, PPColor.Brand, PPColor.Brand, 1.dp)
}

/** 버튼 크기예요. */
enum class PPButtonSize(
    val height: Dp,
    val textStyle: PPType,
    val radius: Dp,
) {
    /** 높이 52, Medium 15(G-04) */
    Large(52.dp, PPType.LabelMd, PPRadius.Small),
    /** 높이 40, Medium 12 */
    Small(40.dp, PPType.LabelSm, PPRadius.Small)
}
