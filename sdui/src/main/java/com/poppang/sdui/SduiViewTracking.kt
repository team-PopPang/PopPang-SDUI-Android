package com.poppang.sdui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.delay

/** 노출 로그 기준이에요. 섹션이 절반 넘게 보인 채 0.5초가 지나면 노출이에요(계획서 예시 기준). */
internal object SduiViewRule {
    /** 보여야 하는 넓이 비율 */
    const val VISIBLE_RATIO = 0.5f

    /** 보인 채로 지나야 하는 시간(밀리초) */
    const val DURATION_MILLIS = 500L

    /** 크기가 width × height인 섹션 중 화면에 보이는 넓이가 절반을 넘는지예요. */
    fun isVisible(width: Float, height: Float, visibleWidth: Float, visibleHeight: Float): Boolean {
        val area = width * height
        if (area <= 0f) return false
        return visibleWidth * visibleHeight >= area * VISIBLE_RATIO
    }
}

/**
 * 노출 기준을 넘으면 한 번 불러요. 다시 가려졌다가 보이면 또 불러서, 몇 번 남길지는 부르는 쪽이 정해요.
 *
 * 보이는 넓이는 스크롤 영역처럼 바깥이 자른 뒤의 창 안 넓이(boundsInWindow)예요.
 */
internal fun Modifier.sduiOnView(onView: () -> Unit): Modifier = composed {
    var visible by remember { mutableStateOf(false) }
    val currentOnView by rememberUpdatedState(onView)
    LaunchedEffect(visible) {
        if (visible) {
            delay(SduiViewRule.DURATION_MILLIS)
            currentOnView()
        }
    }
    Modifier.onGloballyPositioned { coordinates ->
        val bounds = coordinates.boundsInWindow()
        visible = SduiViewRule.isVisible(
            coordinates.size.width.toFloat(),
            coordinates.size.height.toFloat(),
            bounds.width,
            bounds.height,
        )
    }
}
