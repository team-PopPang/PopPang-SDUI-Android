// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 tokens/·elements/를 고쳐요.
package com.poppang.design

// 요소 모양: PPImage(url: String, contentMode: PPImageContentMode = PPImageContentMode.Fill, radius: Dp = PPRadius.Medium, placeholderColor: Color = PPColor.BackgroundSecondary)

/** 이미지를 자리에 맞추는 방법이에요. */
enum class PPImageContentMode {
    /** 자리를 꽉 채우고 넘치는 부분은 잘라요. */
    Fill,
    /** 이미지 전체가 보이게 줄여요. */
    Fit
}
