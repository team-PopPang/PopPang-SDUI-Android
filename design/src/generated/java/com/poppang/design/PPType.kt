// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** 글꼴 역할이에요. 화면과 SDUI 섹션은 이 이름으로 글꼴을 골라요. 0단계 결정 5번의 역할 7개예요. */
enum class PPType(val style: PPFontStyle) {
    /** 팝업 이름 같은 큰 제목. G-06 */
    TitleLg(PPFont.Bold18),
    /** 소제목, 큰 버튼 글자. G-04 */
    LabelMd(PPFont.Medium15),
    /** 작은 버튼 글자 */
    LabelSm(PPFont.Medium12),
    /** 운영 정보, 카드 이름 */
    BodyMd(PPFont.Regular15),
    /**
     * 긴 본문. 줄 간격이 글자 크기 × 0.6(9)이라 바탕에 없어요. 줄 높이는 1.151 + 0.6 = 1.751배예요(G-15).
     * S-Core Dream 400 · 15/26.265 · 자간 0.3
     */
    BodyLong(PPFontStyle("S-Core Dream", FontWeight(400), 15.sp, 26.265.sp, 0.3.sp)),
    /** "1,204명이 봤어요" 같은 짧은 안내, 짧은 주소 */
    CaptionSm(PPFont.Regular12),
    /** 카테고리 태그 */
    CaptionXs(PPFont.Regular11)
}
