// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 요소 모양: PPIcon(name: String, size: PPIconSize = PPIconSize.Md, color: Color = PPColor.TextPrimary)

/** 아이콘 크기예요. 지금은 화면마다 14~25로 제각각이라 세 단계로 시작해요. */
enum class PPIconSize(
    val size: Dp,
) {
    Sm(15.dp),
    Md(20.dp),
    Lg(25.dp)
}
