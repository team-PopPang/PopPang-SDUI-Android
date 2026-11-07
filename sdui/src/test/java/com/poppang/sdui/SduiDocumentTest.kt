package com.poppang.sdui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SduiDocumentTest {
    private fun document(sections: String, version: String = "1.0"): SduiDocument = requireNotNull(
        SduiDocument.decode(
            """{"schemaVersion": "$version", "locale": "ko", "screen": {"id": "popup_detail", "revision": 3}, "sections": $sections}""",
        ),
    )

    @Test
    fun missingDocumentIsNull() {
        assertNull(SduiDocument.decode(null))
    }

    @Test
    fun brokenDocumentIsUnusable() {
        for (json in listOf("not json", "\"not a document\"", "null", "[]")) {
            val document = requireNotNull(SduiDocument.decode(json))
            assertFalse(json, document.isUsable)
            assertEquals(json, listOf(SduiDiagnostic.Code.InvalidDocument), document.diagnostics().map { it.code })
            assertEquals(json, emptyList<SduiSection>(), document.sections())
        }
    }

    @Test
    fun version() {
        assertTrue(document("[]", version = "1.7").isUsable)
        for (version in listOf("2.0", "1", "1.0.0", "v1.0", "1.x", "")) {
            val read = document("[]", version = version)
            assertFalse(version, read.isUsable)
            assertEquals(version, listOf(SduiDiagnostic.Code.UnsupportedVersion), read.diagnostics().map { it.code })
        }
    }

    @Test
    fun fallbackKeepsSourceType() {
        val read = document(
            """[{"id": "a", "type": "review.preview", "fallback": {"type": "notice.banner", "props": {"title": "후기"}}}]""",
        )
        val section = read.sections().single()
        assertEquals("notice.banner", section.type)
        assertEquals("review.preview", section.sourceType)
        assertTrue(section.isFallback)
        assertEquals(SduiDiagnostic(SduiDiagnostic.Code.UnknownType, "a", "review.preview"), read.diagnostics().first())
    }

    @Test
    fun diagnosticNamesTheField() {
        val read = document("""[{"id": "a", "type": "button.cta", "props": {"title": "예약하기"}}]""")
        assertEquals(listOf(SduiDiagnostic(SduiDiagnostic.Code.EmptySection, "a", "button.cta", "action")), read.diagnostics())
    }

    @Test
    fun everyDecodeIsANewDocument() {
        assertNotEquals(document("[]"), document("[]"))
    }

    @Test
    fun logParameters() {
        val read = document(
            """[{"id": "addon_notice", "type": "notice.banner", "props": {"title": "안내"}, "logging": {"experiment": "a", "screen_id": "x", "rank": 2, "skip": [1]}}]""",
        )
        val section = read.sections().single()
        assertEquals(
            mapOf(
                "experiment" to "a",
                "rank" to 2L,
                "screen_id" to "popup_detail",
                "revision" to 3L,
                "section_id" to "addon_notice",
                "section_type" to "notice.banner",
                "position" to 1L,
                "sdk_version" to SduiSdk.VERSION,
                "platform" to "android",
            ),
            read.logParameters(section, position = 1),
        )
    }

    @Test
    fun actionStepsFlattenSequenceAndDropUnknownActions() {
        val action = SduiAction.Sequence(
            listOf(
                SduiAction.Toast("복사했어요"),
                SduiAction.Unsupported("log"),
                SduiAction.Sequence(listOf(SduiAction.Navigate("popup/1"), SduiAction.Copy("서울", null))),
            ),
        )
        assertEquals(
            listOf(SduiAction.Toast("복사했어요"), SduiAction.Navigate("popup/1"), SduiAction.Copy("서울", null)),
            action.steps,
        )
        assertEquals(emptyList<SduiAction>(), SduiAction.Unsupported("log").steps)
        assertEquals("sequence", action.typeName)
    }

    @Test
    fun viewRule() {
        assertTrue(SduiViewRule.isVisible(100f, 100f, 100f, 50f))
        assertFalse(SduiViewRule.isVisible(100f, 100f, 100f, 49f))
        assertFalse(SduiViewRule.isVisible(100f, 100f, 0f, 0f))
        assertFalse(SduiViewRule.isVisible(0f, 0f, 0f, 0f))
    }
}
