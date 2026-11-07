// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** 글꼴 바탕이에요. 두 앱이 지금 쓰는 S-Core Dream 굵기 + 크기 조합이고, 자간 0.02배는 두 앱이 같아요. 줄 높이는 지금 iOS가 그리는 줄 하나의 높이(글꼴 기본 줄 높이 1.151배 + 줄 간격 0.4배 = 1.551배)예요(차이 목록 G-15). 화면은 바탕보다 역할(type)을 먼저 써요. */
object PPFont {
    /** S-Core Dream 300 · 10/15.51 · 자간 0.2 */
    val Light10 = PPFontStyle("S-Core Dream", FontWeight(300), 10.sp, 15.51.sp, 0.2.sp)
    /** S-Core Dream 300 · 12/18.612 · 자간 0.24 */
    val Light12 = PPFontStyle("S-Core Dream", FontWeight(300), 12.sp, 18.612.sp, 0.24.sp)
    /** S-Core Dream 300 · 15/23.265 · 자간 0.3 */
    val Light15 = PPFontStyle("S-Core Dream", FontWeight(300), 15.sp, 23.265.sp, 0.3.sp)
    /** S-Core Dream 400 · 9/13.959 · 자간 0.18 */
    val Regular9 = PPFontStyle("S-Core Dream", FontWeight(400), 9.sp, 13.959.sp, 0.18.sp)
    /** S-Core Dream 400 · 10/15.51 · 자간 0.2 */
    val Regular10 = PPFontStyle("S-Core Dream", FontWeight(400), 10.sp, 15.51.sp, 0.2.sp)
    /** S-Core Dream 400 · 11/17.061 · 자간 0.22 */
    val Regular11 = PPFontStyle("S-Core Dream", FontWeight(400), 11.sp, 17.061.sp, 0.22.sp)
    /** S-Core Dream 400 · 12/18.612 · 자간 0.24 */
    val Regular12 = PPFontStyle("S-Core Dream", FontWeight(400), 12.sp, 18.612.sp, 0.24.sp)
    /** S-Core Dream 400 · 13/20.163 · 자간 0.26 */
    val Regular13 = PPFontStyle("S-Core Dream", FontWeight(400), 13.sp, 20.163.sp, 0.26.sp)
    /** S-Core Dream 400 · 14/21.714 · 자간 0.28 */
    val Regular14 = PPFontStyle("S-Core Dream", FontWeight(400), 14.sp, 21.714.sp, 0.28.sp)
    /** S-Core Dream 400 · 15/23.265 · 자간 0.3 */
    val Regular15 = PPFontStyle("S-Core Dream", FontWeight(400), 15.sp, 23.265.sp, 0.3.sp)
    /** S-Core Dream 500 · 8/12.408 · 자간 0.16 */
    val Medium8 = PPFontStyle("S-Core Dream", FontWeight(500), 8.sp, 12.408.sp, 0.16.sp)
    /** S-Core Dream 500 · 10/15.51 · 자간 0.2 */
    val Medium10 = PPFontStyle("S-Core Dream", FontWeight(500), 10.sp, 15.51.sp, 0.2.sp)
    /** S-Core Dream 500 · 11/17.061 · 자간 0.22 */
    val Medium11 = PPFontStyle("S-Core Dream", FontWeight(500), 11.sp, 17.061.sp, 0.22.sp)
    /** S-Core Dream 500 · 12/18.612 · 자간 0.24 */
    val Medium12 = PPFontStyle("S-Core Dream", FontWeight(500), 12.sp, 18.612.sp, 0.24.sp)
    /** S-Core Dream 500 · 13/20.163 · 자간 0.26 */
    val Medium13 = PPFontStyle("S-Core Dream", FontWeight(500), 13.sp, 20.163.sp, 0.26.sp)
    /** S-Core Dream 500 · 14/21.714 · 자간 0.28 */
    val Medium14 = PPFontStyle("S-Core Dream", FontWeight(500), 14.sp, 21.714.sp, 0.28.sp)
    /** S-Core Dream 500 · 15/23.265 · 자간 0.3 */
    val Medium15 = PPFontStyle("S-Core Dream", FontWeight(500), 15.sp, 23.265.sp, 0.3.sp)
    /** S-Core Dream 500 · 16/24.816 · 자간 0.32 */
    val Medium16 = PPFontStyle("S-Core Dream", FontWeight(500), 16.sp, 24.816.sp, 0.32.sp)
    /** S-Core Dream 500 · 17/26.367 · 자간 0.34 */
    val Medium17 = PPFontStyle("S-Core Dream", FontWeight(500), 17.sp, 26.367.sp, 0.34.sp)
    /** S-Core Dream 500 · 18/27.918 · 자간 0.36 */
    val Medium18 = PPFontStyle("S-Core Dream", FontWeight(500), 18.sp, 27.918.sp, 0.36.sp)
    /** S-Core Dream 600 · 10/15.51 · 자간 0.2 */
    val Bold10 = PPFontStyle("S-Core Dream", FontWeight(600), 10.sp, 15.51.sp, 0.2.sp)
    /** S-Core Dream 600 · 11/17.061 · 자간 0.22 */
    val Bold11 = PPFontStyle("S-Core Dream", FontWeight(600), 11.sp, 17.061.sp, 0.22.sp)
    /** S-Core Dream 600 · 12/18.612 · 자간 0.24 */
    val Bold12 = PPFontStyle("S-Core Dream", FontWeight(600), 12.sp, 18.612.sp, 0.24.sp)
    /** S-Core Dream 600 · 13/20.163 · 자간 0.26 */
    val Bold13 = PPFontStyle("S-Core Dream", FontWeight(600), 13.sp, 20.163.sp, 0.26.sp)
    /** S-Core Dream 600 · 14/21.714 · 자간 0.28 */
    val Bold14 = PPFontStyle("S-Core Dream", FontWeight(600), 14.sp, 21.714.sp, 0.28.sp)
    /** S-Core Dream 600 · 15/23.265 · 자간 0.3 */
    val Bold15 = PPFontStyle("S-Core Dream", FontWeight(600), 15.sp, 23.265.sp, 0.3.sp)
    /** S-Core Dream 600 · 16/24.816 · 자간 0.32 */
    val Bold16 = PPFontStyle("S-Core Dream", FontWeight(600), 16.sp, 24.816.sp, 0.32.sp)
    /** S-Core Dream 600 · 17/26.367 · 자간 0.34 */
    val Bold17 = PPFontStyle("S-Core Dream", FontWeight(600), 17.sp, 26.367.sp, 0.34.sp)
    /** S-Core Dream 600 · 18/27.918 · 자간 0.36 */
    val Bold18 = PPFontStyle("S-Core Dream", FontWeight(600), 18.sp, 27.918.sp, 0.36.sp)
    /** S-Core Dream 600 · 20/31.02 · 자간 0.4 */
    val Bold20 = PPFontStyle("S-Core Dream", FontWeight(600), 20.sp, 31.02.sp, 0.4.sp)
    /** S-Core Dream 700 · 13/20.163 · 자간 0.26 */
    val ExtraBold13 = PPFontStyle("S-Core Dream", FontWeight(700), 13.sp, 20.163.sp, 0.26.sp)
    /** S-Core Dream 700 · 14/21.714 · 자간 0.28 */
    val ExtraBold14 = PPFontStyle("S-Core Dream", FontWeight(700), 14.sp, 21.714.sp, 0.28.sp)
    /** S-Core Dream 700 · 15/23.265 · 자간 0.3 */
    val ExtraBold15 = PPFontStyle("S-Core Dream", FontWeight(700), 15.sp, 23.265.sp, 0.3.sp)
    /** S-Core Dream 700 · 18/27.918 · 자간 0.36 */
    val ExtraBold18 = PPFontStyle("S-Core Dream", FontWeight(700), 18.sp, 27.918.sp, 0.36.sp)
    /** S-Core Dream 700 · 24/37.224 · 자간 0.48 */
    val ExtraBold24 = PPFontStyle("S-Core Dream", FontWeight(700), 24.sp, 37.224.sp, 0.48.sp)
    /** S-Core Dream 700 · 26/40.326 · 자간 0.52 */
    val ExtraBold26 = PPFontStyle("S-Core Dream", FontWeight(700), 26.sp, 40.326.sp, 0.52.sp)
    /** S-Core Dream 900 · 20/31.02 · 자간 0.4 */
    val Black20 = PPFontStyle("S-Core Dream", FontWeight(900), 20.sp, 31.02.sp, 0.4.sp)
}
