package com.poppang.sdui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poppang.design.LocalPPTextLocale
import com.poppang.design.PPSpacing

/**
 * 문서의 섹션을 위에서부터 차례로 그려요. 스크롤이 없어서 네이티브 스크롤(verticalScroll) 안에 놓아요.
 *
 * 섹션 사이 간격(s20), 좌우 여백, 노출·클릭 로그, 진단 로그, 테스트 태그(`sdui.{화면}.{섹션 id}`, 리소스 id로도 보여요)를
 * 여기서 처리해요. 문서를 쓸 수 없거나(`isUsable`이 false) 그릴 섹션이 없으면 아무것도 그리지 않아요.
 *
 * ```kotlin
 * sdui?.let { document ->
 *     SduiSections(document, area = "popup_detail.addons", onAction = viewModel::onSduiAction)
 * }
 * ```
 *
 * @param document 응답을 받은 곳에서 읽어 둔 문서
 * @param area 그릴 자리예요(예: popup_detail.addons). null이면 자리와 상관없이 모두 그려요.
 * @param onAction 섹션을 누르면 받는 액션이에요. sequence는 펼쳐서 차례로, 모르는 액션은 빼고 넘겨요.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SduiSections(
    document: SduiDocument,
    modifier: Modifier = Modifier,
    area: String? = null,
    onAction: (SduiAction) -> Unit,
) {
    val environment = LocalSduiEnvironment.current
    val record = remember { SduiLogRecord() }
    val sections = remember(document, area) { document.sections(area) }
    val screenId = document.screen?.id ?: "unknown"

    LaunchedEffect(document, area) {
        if (record.markDiagnosed(document.id, area)) {
            for (diagnostic in document.diagnostics(area)) {
                environment.log(SduiLogEvent.Diagnostic(diagnostic, document.logParameters(diagnostic)))
            }
        }
    }

    // 서버 문구의 언어로 글자를 채워요(한자 등). 문서에 언어가 없으면 앱 언어 그대로예요.
    CompositionLocalProvider(LocalPPTextLocale provides (document.locale ?: LocalPPTextLocale.current)) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .semantics { testTagsAsResourceId = true },
            verticalArrangement = Arrangement.spacedBy(PPSpacing.S20),
        ) {
            sections.forEachIndexed { index, section ->
                val position = index + 1
                key(section.id) {
                    Box(
                        Modifier
                            .testTag("sdui.$screenId.${section.id}")
                            .sduiOnView {
                                if (record.markViewed(document.id, section.id)) {
                                    environment.log(SduiLogEvent.SectionView(document.logParameters(section, position)))
                                }
                            },
                    ) {
                        SduiSectionView(section) { action ->
                            val steps = action.steps
                            if (steps.isNotEmpty()) {
                                val parameters = document.logParameters(section, position) + ("action_type" to action.typeName)
                                environment.log(SduiLogEvent.Action(action, parameters))
                                steps.forEach(onAction)
                            }
                        }
                        if (environment.debugOverlay) {
                            SduiDebugLabel(section, document.screen?.revision)
                        }
                    }
                }
            }
        }
    }
}

/** 섹션 type마다 그리는 컴포저블을 골라요. 새 섹션을 Spec에 더하면 여기서 컴파일이 멈춰서 그리는 코드를 빠뜨리지 않아요. */
@Composable
internal fun SduiSectionView(section: SduiSection, perform: (SduiAction) -> Unit) {
    when (val props = section.props) {
        is SduiSectionProps.NoticeBanner -> SduiNoticeBannerSection(props.props, perform)
        is SduiSectionProps.ButtonCta -> SduiButtonCtaSection(props.props, perform)
        is SduiSectionProps.ImageBanner -> SduiImageBannerSection(props.props, perform)
    }
}

/** 이미 남긴 로그를 기억해요. 문서를 받을 때마다(문서 id가 바뀔 때마다) 처음부터 다시 세요. */
internal class SduiLogRecord {
    private var document: String? = null
    private val viewed = mutableSetOf<String>()
    private val diagnosed = mutableSetOf<Pair<String, String?>>()

    /** 처음 본 섹션이면 true예요. */
    fun markViewed(document: String, sectionId: String): Boolean {
        if (this.document != document) {
            this.document = document
            viewed.clear()
        }
        return viewed.add(sectionId)
    }

    /** 이 문서·자리의 진단을 처음 남기면 true예요. */
    fun markDiagnosed(document: String, area: String?): Boolean = diagnosed.add(document to area)
}

/** 디버그 라벨이에요. 섹션 위에 type·id·리비전을 적고, fallback으로 바뀐 섹션은 빨간 테두리를 둘러요. 글자 크기 설정과 상관없이 9로 그려요. */
@Composable
private fun BoxScope.SduiDebugLabel(section: SduiSection, revision: Int?) {
    val fallback = if (section.isFallback) " · fallback(${section.sourceType})" else ""
    val density = LocalDensity.current
    Box(
        Modifier
            .matchParentSize()
            .then(if (section.isFallback) Modifier.border(2.dp, Color.Red) else Modifier)
            .clearAndSetSemantics {},
    ) {
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
            BasicText(
                text = "${section.type} · ${section.id} · r${revision ?: "-"}$fallback",
                style = TextStyle(color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace),
                modifier = Modifier
                    .background(if (section.isFallback) Color.Red.copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
    }
}
