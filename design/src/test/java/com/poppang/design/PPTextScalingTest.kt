package com.poppang.design

import org.junit.Assert.assertEquals
import org.junit.Test

class PPTextScalingTest {
    @Test
    fun fixedIgnoresSystemSetting() {
        for (fontScale in listOf(0.85f, 1f, 1.3f, 2f)) {
            assertEquals(1f, PPTextScaling.Fixed.scale(fontScale))
        }
    }

    @Test
    fun systemFollowsUpTo1_3() {
        assertEquals(0.85f, PPTextScaling.System.scale(0.85f))
        assertEquals(1f, PPTextScaling.System.scale(1f))
        assertEquals(1.15f, PPTextScaling.System.scale(1.15f))
        assertEquals(1.3f, PPTextScaling.System.scale(1.3f))
        assertEquals(1.3f, PPTextScaling.System.scale(2f))
    }

    @Test
    fun imageRequestContentScale() {
        assertEquals(androidx.compose.ui.layout.ContentScale.Crop, PPImageRequest("https://a.com/a.jpg", PPImageContentMode.Fill).contentScale)
        assertEquals(androidx.compose.ui.layout.ContentScale.Fit, PPImageRequest("https://a.com/a.jpg", PPImageContentMode.Fit).contentScale)
    }
}
