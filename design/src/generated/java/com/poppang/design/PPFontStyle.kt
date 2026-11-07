// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

/** 글꼴 토큰 값이에요. 크기·줄 높이·자간은 sp이고, 화면에 입히는 방법은 디자인 모듈이 정해요. */
data class PPFontStyle(
    /** 글꼴 이름이에요. */
    val family: String,
    /** 굵기예요. S-Core Dream 파일 번호 × 100이에요(Bold 6 = W600). */
    val weight: FontWeight,
    /** 글자 크기예요. */
    val size: TextUnit,
    /** 줄 높이예요. 글자 크기 × 줄 높이 배수예요. */
    val lineHeight: TextUnit,
    /** 자간이에요. */
    val letterSpacing: TextUnit,
)
