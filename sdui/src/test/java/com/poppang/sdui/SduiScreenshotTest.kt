package com.poppang.sdui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.poppang.design.LocalPPTextLocale
import com.poppang.design.PPButton
import com.poppang.design.PPButtonSize
import com.poppang.design.PPButtonStyle
import com.poppang.design.PPColor
import com.poppang.design.PPEnvironment
import com.poppang.design.PPIcon
import com.poppang.design.PPIconSize
import com.poppang.design.PPImage
import com.poppang.design.PPImageContentMode
import com.poppang.design.PPImageRequest
import com.poppang.design.PPPlaceholder
import com.poppang.design.PPProvider
import com.poppang.design.PPRadius
import com.poppang.design.PPSpacing
import com.poppang.design.PPText
import com.poppang.design.PPTextScaling
import com.poppang.design.PPType
import java.util.Locale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.float
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Spec screenshots/scenes.json의 장면을 찍어요. iOS(SDUIScreenshotTests)와 같은 장면·폭·해상도예요.
 *
 * poppang.screenshotDir 시스템 속성이 있을 때만 찍어요. make screenshots가 -PscreenshotDir로 넘기고,
 * make android가 장면 목록과 가짜 이미지를 test/resources/screenshots에 넣어요.
 * 화면은 폭 390dp·xxhdpi(3배)예요. Robolectric은 SDK 35부터 JDK 21이 필요해서 SDK 34로 찍어요.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w390dp-h2400dp-xxhdpi")
class SduiScreenshotTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun renderScenes() {
        val output = System.getProperty("poppang.screenshotDir")
        Assume.assumeFalse("poppang.screenshotDir가 없어서 찍지 않아요. make screenshots로 찍어요.", output.isNullOrEmpty())
        val loader = requireNotNull(javaClass.classLoader)
        val catalog = Json.parseToJsonElement(
            requireNotNull(loader.getResourceAsStream("screenshots/scenes.json")) { "make android로 장면 목록을 넣어요." }
                .bufferedReader().use { it.readText() },
        ).jsonObject
        val fake = requireNotNull(loader.getResourceAsStream("screenshots/fake-image.png"))
            .use { BitmapFactory.decodeStream(it) }
            .asImageBitmap()
        val locale = catalog.getValue("locale").jsonPrimitive.content
        val width = catalog.getValue("width").jsonPrimitive.float
        val scales = catalog.getValue("scales").jsonArray.map { it.jsonPrimitive.float }
        val scenes = catalog.getValue("scenes").jsonArray.map { it.jsonObject }

        var current by mutableStateOf<Pair<JsonObject, Float>?>(null)
        compose.setContent {
            current?.let { (scene, scale) ->
                // 1.3배는 기기 글자 배율을 1.3으로 둔 것이에요. SDK(PPTextScaling.System)가 1.3배까지 따라요.
                val density = LocalDensity.current
                CompositionLocalProvider(LocalDensity provides Density(density.density, scale), LocalPPTextLocale provides locale) {
                    PPProvider(PPEnvironment(image = { request -> FakeImage(request, fake) }, textScaling = PPTextScaling.System)) {
                        Box(Modifier.testTag(SCENE).width(width.dp).background(PPColor.Background)) {
                            ScreenshotScene(scene, catalog)
                        }
                    }
                }
            }
        }
        for (scene in scenes) {
            for (scale in scales) {
                current = scene to scale
                compose.waitForIdle()
                val name = "${scene.getValue("id").jsonPrimitive.content}@${String.format(Locale.ROOT, "%.1f", scale)}x.png"
                compose.onNodeWithTag(SCENE).captureRoboImage("$output/$name")
            }
        }
    }

    private companion object {
        const val SCENE = "scene"
    }
}

/** 두 플랫폼이 같은 가짜 이미지를 받은 contentMode대로 그려요. */
@Composable
private fun FakeImage(request: PPImageRequest, image: ImageBitmap) {
    Image(bitmap = image, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = request.contentScale)
}

@Composable
private fun ScreenshotScene(scene: JsonObject, catalog: JsonObject) {
    val sections = scene["sections"] as? JsonArray
    if (sections != null) {
        val document = remember(scene) {
            SduiDocument.from(
                buildJsonObject {
                    put("schemaVersion", "1.0")
                    put("locale", "ko")
                    putJsonObject("screen") {
                        put("id", "screenshot")
                        put("revision", 0)
                    }
                    put("sections", sections)
                },
            )
        }
        SduiSections(document, Modifier.padding(vertical = PPSpacing.S20)) {}
        return
    }
    val padding = catalog.getValue("padding").jsonPrimitive.float.dp
    val spacing = catalog.getValue("spacing").jsonPrimitive.float.dp
    val element = scene["element"]?.jsonPrimitive?.contentOrNull
    val cases = scene.getValue("cases").jsonArray.map { it.jsonObject }
    if (scene["layout"]?.jsonPrimitive?.contentOrNull == "row") {
        Row(
            Modifier.fillMaxWidth().padding(padding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            cases.forEach { ScreenshotCase(element, it) }
        }
    } else {
        Column(Modifier.fillMaxWidth().padding(padding), verticalArrangement = Arrangement.spacedBy(spacing)) {
            cases.forEach { ScreenshotCase(element, it) }
        }
    }
}

@Composable
private fun ScreenshotCase(element: String?, case: JsonObject) {
    fun text(key: String): String? = case[key]?.jsonPrimitive?.contentOrNull
    fun number(key: String): Float? = case[key]?.jsonPrimitive?.floatOrNull
    when (element) {
        "PPText" -> PPText(
            text("text") ?: "",
            style = entry(PPType.entries, text("style")) ?: PPType.BodyMd,
            color = roleColor(text("color")) ?: PPColor.TextPrimary,
            maxLines = case["maxLines"]?.jsonPrimitive?.intOrNull,
        )
        "PPIcon" -> PPIcon(
            text("name") ?: "",
            size = entry(PPIconSize.entries, text("size")) ?: PPIconSize.Md,
            color = roleColor(text("color")) ?: PPColor.TextPrimary,
        )
        "PPButton" -> PPButton(
            text("title") ?: "",
            style = entry(PPButtonStyle.entries, text("style")) ?: PPButtonStyle.Primary,
            size = entry(PPButtonSize.entries, text("size")) ?: PPButtonSize.Large,
        ) {}
        "PPImage" -> PPImage(
            text("url") ?: "",
            Modifier.size((number("width") ?: 0f).dp, (number("height") ?: 0f).dp),
            contentMode = entry(PPImageContentMode.entries, text("contentMode")) ?: PPImageContentMode.Fill,
            radius = number("radius")?.dp ?: PPRadius.Medium,
        )
        "PPPlaceholder" -> PPPlaceholder(
            height = (number("height") ?: 0f).dp,
            width = number("width")?.dp,
            radius = number("radius")?.dp ?: PPRadius.Small,
        )
    }
}

/** 장면 목록의 이름(titleLg)으로 enum 값(TitleLg)을 찾아요. */
private fun <E : Enum<E>> entry(entries: List<E>, name: String?): E? =
    entries.firstOrNull { it.name.replaceFirstChar { first -> first.lowercaseChar() } == name }

/** 역할 색 이름으로 색을 골라요. */
private fun roleColor(name: String?): Color? = when (name) {
    "brand" -> PPColor.Brand
    "brandSubtle" -> PPColor.BrandSubtle
    "textPrimary" -> PPColor.TextPrimary
    "textStrong" -> PPColor.TextStrong
    "textSecondary" -> PPColor.TextSecondary
    "textTertiary" -> PPColor.TextTertiary
    "textOnBrand" -> PPColor.TextOnBrand
    "background" -> PPColor.Background
    "backgroundSecondary" -> PPColor.BackgroundSecondary
    "border" -> PPColor.Border
    "divider" -> PPColor.Divider
    "error" -> PPColor.Error
    "success" -> PPColor.Success
    else -> null
}
