package com.poppang.sdui

import java.io.File
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Spec 픽스처(fixtures/)를 해석 엔진으로 읽어 기대 결과와 비교해요. make android가 픽스처를 test/resources/fixtures에 넣어요. */
class SduiFixtureTest {
    @Test
    fun everyFixtureMatchesExpectedResult() {
        val url = requireNotNull(javaClass.classLoader?.getResource("fixtures")) { "make android로 픽스처를 넣어요." }
        val files = File(url.toURI()).listFiles { file -> file.extension == "json" }.orEmpty().sortedBy { it.name }
        assertTrue("픽스처가 18개보다 적어요: ${files.size}", files.size >= 18)
        for (file in files) {
            val fixture = Json.parseToJsonElement(file.readText()).jsonObject
            val area = fixture.text("area")
            val document = SduiDocument.from(fixture.getValue("document"))
            val expected = fixture.getValue("expected").jsonObject
            assertEquals(
                file.name,
                expected.getValue("rendered").jsonArray.map { "${it.jsonObject.text("id")}:${it.jsonObject.text("type")}" },
                document.sections(area).map { "${it.id}:${it.type}" },
            )
            assertEquals(
                file.name,
                expected.getValue("diagnostics").jsonArray.map { "${it.jsonObject.text("code")}(${it.jsonObject.text("section") ?: "-"})" },
                document.diagnostics(area).map { "${it.code.value}(${it.sectionId ?: "-"})" },
            )
        }
    }

    private fun JsonObject.text(key: String): String? = (this[key] as? JsonPrimitive)?.contentOrNull
}
