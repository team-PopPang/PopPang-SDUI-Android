package com.poppang.sdui.demo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.poppang.design.PPColor
import com.poppang.design.PPEnvironment
import com.poppang.design.PPProvider
import com.poppang.design.PPTextScaling
import com.poppang.sdui.SduiAction
import com.poppang.sdui.SduiEnvironment
import com.poppang.sdui.SduiOpenUrlTarget
import com.poppang.sdui.SduiProvider

/** SDK 데모 앱이에요. 서버 없이 Spec 픽스처와 예시 문서로 섹션과 요소를 그려 봐요. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { DemoApp() }
    }
}

/** 미리 보는 글자 크기예요. 1.3배는 기기 설정을 가장 크게 바꾼 것과 같아요(SDK가 1.3배로 묶어요). */
enum class DemoTextSize(val title: String, val fontScale: Float?) {
    Standard("1.0배", 1f),
    Large("1.3배(가장 크게)", 2f),
    Device("기기 설정 그대로", null),
}

/** 데모 설정과 받은 로그예요. */
class DemoSettings {
    var textSize by mutableStateOf(DemoTextSize.Standard)
    var textScaling by mutableStateOf(PPTextScaling.System)
    var fakeImages by mutableStateOf(true)
    var debugOverlay by mutableStateOf(false)
    var events by mutableStateOf(listOf<String>())
}

private enum class DemoTab(val title: String) {
    Showcase("상세 예시"),
    Fixtures("픽스처"),
    Elements("요소"),
    Log("로그"),
}

@Composable
private fun DemoApp() {
    val settings = remember { DemoSettings() }
    var tab by rememberSaveable { mutableStateOf(DemoTab.Showcase) }
    val context = LocalContext.current
    // 앱의 공통 액션 처리기 자리예요. 데모는 무엇을 할지 토스트로 보여 줘요.
    val handle: (SduiAction) -> Unit = { action ->
        val message = when (action) {
            is SduiAction.Navigate -> "이동: ${action.route}"
            is SduiAction.OpenUrl -> "${if (action.target == SduiOpenUrlTarget.External) "외부 앱" else "앱 안 브라우저"}로 열기: ${action.url}"
            is SduiAction.Copy -> "복사: ${action.text}${action.toast?.let { " · $it" } ?: ""}"
            is SduiAction.Toast -> action.message
            is SduiAction.Sequence, is SduiAction.Unsupported -> null // SDK가 펼치거나 빼서 넘겨서 오지 않아요.
        }
        message?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    val density = LocalDensity.current
    val fontScale = settings.textSize.fontScale ?: density.fontScale

    MaterialTheme(colorScheme = lightColorScheme(primary = PPColor.Brand)) {
        PPProvider(
            if (settings.fakeImages) {
                PPEnvironment(image = { DemoImage(it) }, textScaling = settings.textScaling)
            } else {
                PPEnvironment(textScaling = settings.textScaling)
            },
        ) {
            SduiProvider(
                SduiEnvironment(
                    log = { event ->
                        val parameters = event.parameters.entries.sortedBy { it.key }.joinToString(" ") { "${it.key}=${it.value}" }
                        settings.events = listOf("${event.name ?: "진단"} $parameters") + settings.events
                    },
                    debugOverlay = settings.debugOverlay,
                ),
            ) {
                CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                DemoTab.entries.forEach { item ->
                                    NavigationBarItem(
                                        selected = tab == item,
                                        onClick = { tab = item },
                                        icon = {},
                                        label = { Text(item.title) },
                                    )
                                }
                            }
                        },
                    ) { padding ->
                        Box(Modifier.fillMaxSize().padding(padding)) {
                            when (tab) {
                                DemoTab.Showcase -> ShowcaseScreen(settings, handle)
                                DemoTab.Fixtures -> FixturesScreen(settings, handle)
                                DemoTab.Elements -> ElementsScreen(settings)
                                DemoTab.Log -> LogScreen(settings)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 화면 위 설정 버튼이에요. 글자 크기, 이미지, 디버그 표시를 바꿔요. */
@Composable
fun DemoSettingsButton(settings: DemoSettings) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("설정")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DemoTextSize.entries.forEach { size ->
                DropdownMenuItem(
                    text = { Text((if (settings.textSize == size) "✓ " else "") + "글자 ${size.title}") },
                    onClick = { settings.textSize = size; expanded = false },
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text((if (settings.textScaling == PPTextScaling.System) "✓ " else "") + "설정을 따름(1.3배까지)") },
                onClick = { settings.textScaling = PPTextScaling.System; expanded = false },
            )
            DropdownMenuItem(
                text = { Text((if (settings.textScaling == PPTextScaling.Fixed) "✓ " else "") + "1.0배 고정") },
                onClick = { settings.textScaling = PPTextScaling.Fixed; expanded = false },
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text((if (settings.fakeImages) "✓ " else "") + "가짜 이미지") },
                onClick = { settings.fakeImages = !settings.fakeImages; expanded = false },
            )
            DropdownMenuItem(
                text = { Text((if (settings.debugOverlay) "✓ " else "") + "디버그 표시") },
                onClick = { settings.debugOverlay = !settings.debugOverlay; expanded = false },
            )
        }
    }
}
