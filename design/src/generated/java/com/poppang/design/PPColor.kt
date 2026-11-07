// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

/** 역할 색이에요. 화면은 이 이름으로 색을 골라요. iOS DSKit PPColor와 이름이 같고, 값은 0단계 결정에 따라 두 곳(textOnBrand·brandSubtle)이 바뀌었어요. */
object PPColor {
    /** 주요 버튼 배경, 강조 글자·아이콘 */
    val Brand = PPPalette.Orange500
    /**
     * 브랜드 색을 옅게 깐 배경
     * 차이 목록 G-10
     */
    val BrandSubtle = PPPalette.Orange50
    /** 본문 글자 */
    val TextPrimary = PPPalette.Gray800
    /** 가장 진한 글자 */
    val TextStrong = PPPalette.Black
    /** 보조 설명 글자 */
    val TextSecondary = PPPalette.Gray500
    /** 안내·비활성 글자 */
    val TextTertiary = PPPalette.Gray300
    /**
     * 브랜드 색 배경 위의 글자
     * 차이 목록 G-05
     */
    val TextOnBrand = PPPalette.White
    /** 기본 화면 배경 */
    val Background = PPPalette.White
    /** 구역을 나누는 옅은 회색 배경 */
    val BackgroundSecondary = PPPalette.Gray50
    /** 입력창·카드 테두리 */
    val Border = PPPalette.Gray200
    /** 구분선 */
    val Divider = PPPalette.Gray100
    /** 오류 안내 */
    val Error = PPPalette.Red600
    /** 성공 안내 */
    val Success = PPPalette.Green800
}
