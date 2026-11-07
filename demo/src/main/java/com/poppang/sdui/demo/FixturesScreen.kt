package com.poppang.sdui.demo

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poppang.design.PPColor
import com.poppang.design.PPSpacing
import com.poppang.sdui.SduiAction
import com.poppang.sdui.SduiDocument
import com.poppang.sdui.SduiSections
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/** Spec 픽스처를 하나씩 그려 봐요. 기대 결과와 SDK가 낸 결과를 같이 보여 줘요. */
@Composable
fun FixturesScreen(settings: DemoSettings, onAction: (SduiAction) -> Unit) {
    val context = LocalContext.current
    val fixtures = remember { DemoFixture.load(context) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    val fixture = fixtures.firstOrNull { it.name == selected }
    if (fixture != null) {
        BackHandler { selected = null }
        FixtureDetail(fixture, settings, onAction) { selected = null }
        return
    }
    Column(Modifier.fillMaxSize()) {
        DemoTopBar(title = "픽스처 ${fixtures.size}개", settings = settings)
        LazyColumn {
            items(fixtures, key = { it.name }) { item ->
                Column(
                    Modifier.fillMaxWidth().clickable { selected = item.name }.padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    Text(item.name, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
                    Text(item.description, fontSize = 12.sp, color = Color.Gray)
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun FixtureDetail(fixture: DemoFixture, settings: DemoSettings, onAction: (SduiAction) -> Unit, onBack: () -> Unit) {
    val document = remember(fixture.name) { SduiDocument.from(fixture.document) }
    val rendered = document.sections(fixture.area).map { "${it.id}: ${it.type}" }
    val diagnostics = document.diagnostics(fixture.area).map { "${it.code.value}(${it.sectionId ?: "-"})" }
    Column(Modifier.fillMaxSize().background(PPColor.Background)) {
        DemoTopBar(title = fixture.name, settings = settings) {
            TextButton(onClick = onBack) { Text("뒤로") }
        }
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(vertical = PPSpacing.S20),
            verticalArrangement = Arrangement.spacedBy(PPSpacing.S20),
        ) {
            SduiSections(document, area = fixture.area, onAction = onAction)
            Column(Modifier.padding(horizontal = PPSpacing.ScreenHorizontal), verticalArrangement = Arrangement.spacedBy(PPSpacing.S8)) {
                Result("그린 섹션", rendered, fixture.expectedRendered)
                Result("진단", diagnostics, fixture.expectedDiagnostics)
            }
        }
    }
}

@Composable
private fun Result(title: String, actual: List<String>, expected: List<String>) {
    val same = actual == expected
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            (if (same) "✓ " else "✗ ") + title,
            color = if (same) PPColor.Success else PPColor.Error,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
        )
        Text(if (actual.isEmpty()) "없음" else actual.joinToString("\n"), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        if (!same) {
            Text("기대: ${expected.joinToString(", ")}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
        }
    }
}

/** 앱에 넣은 Spec 픽스처 파일이에요(make android가 assets/fixtures/에 넣어요). */
class DemoFixture(
    val name: String,
    val description: String,
    val area: String?,
    val document: JsonElement,
    val expectedRendered: List<String>,
    val expectedDiagnostics: List<String>,
) {
    companion object {
        fun load(context: Context): List<DemoFixture> =
            context.assets.list("fixtures").orEmpty().filter { it.endsWith(".json") }.sorted().map { file ->
                val root = Json.parseToJsonElement(context.assets.open("fixtures/$file").bufferedReader().use { it.readText() }).jsonObject
                val expected = root.getValue("expected").jsonObject
                DemoFixture(
                    name = file.removeSuffix(".json"),
                    description = root.text("description") ?: "",
                    area = root.text("area"),
                    document = root["document"] ?: JsonNull,
                    expectedRendered = expected.getValue("rendered").jsonArray.map { "${it.jsonObject.text("id")}: ${it.jsonObject.text("type")}" },
                    expectedDiagnostics = expected.getValue("diagnostics").jsonArray.map {
                        "${it.jsonObject.text("code")}(${it.jsonObject.text("section") ?: "-"})"
                    },
                )
            }

        private fun JsonObject.text(key: String): String? = (this[key] as? JsonPrimitive)?.contentOrNull
    }
}
