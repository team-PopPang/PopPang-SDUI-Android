// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

// 요소 모양: PPPlaceholder(width: Dp? = null, height: Dp, radius: Dp = PPRadius.Small)

/** 고정 값이에요. PPPlaceholder에서 써요. */
object PPPlaceholderSpec {
    /** 블록 색 */
    val Fill = PPColor.Divider
    /** 지나가는 빛의 색 */
    val ShimmerHighlight = PPColor.Background
    /** 빛 색의 불투명도 */
    val ShimmerOpacity = 0.7
    /** 빛 띠의 폭. 묶음 폭에 대한 비율이에요. */
    val ShimmerBandRatio = 0.6
    /** 빛이 한 번 지나가는 시간(초) */
    val ShimmerDuration = 1.3
}
