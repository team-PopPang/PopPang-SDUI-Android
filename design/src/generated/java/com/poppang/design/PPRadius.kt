// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.unit.dp

/** 모서리 둥글기예요. small·medium·large는 iOS DSKit PPRadius와 이름·값이 같고, xLarge와 full은 0단계 결정 6번으로 더했어요. */
object PPRadius {
    /** 버튼·입력창 */
    val Small = 5.dp
    /** 카드·이미지 */
    val Medium = 10.dp
    /** 시트·큰 카드 */
    val Large = 16.dp
    /** 큰 둥근 카드 */
    val XLarge = 20.dp
    /** 알약·원 모양. 크기와 상관없이 양 끝이 반원이 돼요. */
    val Full = 9999.dp
}
