// src/engine/ 파일은 Kotlin과 kotlinx.serialization만 써요. make test-kotlin이 Android 없이 이 파일로 픽스처를 돌려요.
package com.poppang.sdui

import java.util.UUID
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull

/**
 * 서버가 보낸 SDUI 문서예요.
 *
 * 응답을 받은 곳(뷰모델)에서 한 번 읽어 화면 상태에 두고, 화면은 `SduiSections`에 넘기기만 해요.
 * 섹션마다 따로 읽어서 읽지 못한 섹션만 빼고 진단으로 남겨요. 읽기는 실패를 밖으로 던지지 않아서,
 * 문서가 깨져도 응답의 다른 값은 그대로 읽혀요. 읽는 규칙은 Spec fixtures/README.md의 해석 규칙이에요.
 *
 * ```kotlin
 * // Gson DTO의 sdui: JsonObject?를 글자로 넘겨요. 문서가 없으면 null이에요.
 * _sdui.value = SduiDocument.decode(response.sdui?.toString())
 * ```
 */
class SduiDocument private constructor(
    /** 받을 때마다 새로 붙는 값이에요. 노출 로그를 문서를 받을 때마다 다시 남기는 데 써요. 같은 문서인지도 이 값으로 봐요. */
    val id: String,
    /** 문서 문법 버전(major.minor)이에요. */
    val schemaVersion: String?,
    /** 문구를 만든 언어(ko·en·ja)예요. */
    val locale: String?,
    /** 화면 이름과 발행본 번호예요. */
    val screen: SduiScreenInfo?,
    /** 이 문서로 그릴 수 있는지예요. false면(버전이 다르거나 문서 모양이 틀림) 앱은 같은 응답으로 네이티브를 그려요. */
    val isUsable: Boolean,
    private val documentDiagnostics: List<SduiDiagnostic>,
    private val entries: List<Entry>,
) {
    /** 그릴 섹션이에요. 그릴 순서대로예요. [area]를 주면 그 자리 섹션만, null이면 자리와 상관없이 모두예요. */
    fun sections(area: String? = null): List<SduiSection> {
        if (!isUsable) return emptyList()
        return entries.mapNotNull { entry ->
            if (entry is Entry.Read && (area == null || entry.area == area)) entry.section else null
        }
    }

    /**
     * 문서를 읽다가 생긴 진단이에요. 생긴 순서대로예요.
     * [area]를 주면 다른 자리 섹션의 진단은 빼요. id·type이 없거나 id가 겹친 섹션의 진단은 자리와 상관없이 넣어요.
     */
    fun diagnostics(area: String? = null): List<SduiDiagnostic> =
        documentDiagnostics + entries.flatMap { entry ->
            when (entry) {
                is Entry.Rejected -> listOf(entry.diagnostic)
                is Entry.Read -> if (area == null || entry.area == area) entry.diagnostics else emptyList()
            }
        }

    override fun equals(other: Any?): Boolean = other is SduiDocument && other.id == id

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "SduiDocument(id=$id, screen=$screen, isUsable=$isUsable)"

    /** 섹션을 읽은 결과예요. 문서에 나온 순서대로 둬요. */
    internal sealed interface Entry {
        /** id나 type이 없거나 id가 겹친 섹션이에요. 자리와 상관없이 진단을 남겨요. */
        data class Rejected(val diagnostic: SduiDiagnostic) : Entry

        /** 읽은 섹션이에요. section이 null이면 그리지 않아요. */
        data class Read(val area: String?, val section: SduiSection?, val diagnostics: List<SduiDiagnostic>) : Entry
    }

    private class Outcome(val props: SduiSectionProps?, val mayFallBack: Boolean)

    companion object {
        /** 이 SDK가 읽는 문서 문법의 major 버전이에요. */
        const val SUPPORTED_MAJOR_VERSION = 1

        /** JSON 글자로 읽어요. null이면 문서가 없어서 null이고, JSON이 아니면 invalidDocument인 문서예요. */
        fun decode(json: String?): SduiDocument? {
            if (json == null) return null
            val element = try {
                Json.parseToJsonElement(json)
            } catch (error: IllegalArgumentException) {
                // kotlinx.serialization의 SerializationException도 IllegalArgumentException이에요.
                JsonNull
            }
            return from(element)
        }

        /** 읽어 둔 JSON 값으로 읽어요. 해석 규칙의 참고 구현은 Spec generator/sdui.py의 interpret예요. */
        fun from(json: JsonElement): SduiDocument {
            val id = UUID.randomUUID().toString()
            val root = json as? JsonObject
                ?: return SduiDocument(id, null, null, null, false, listOf(SduiDiagnostic(SduiDiagnostic.Code.InvalidDocument)), emptyList())
            val schemaVersion = root.string("schemaVersion")
            val locale = root.string("locale")
            val screen = screen(root["screen"])
            fun unusable(code: SduiDiagnostic.Code) =
                SduiDocument(id, schemaVersion, locale, screen, false, listOf(SduiDiagnostic(code)), emptyList())
            if (majorVersion(schemaVersion) != SUPPORTED_MAJOR_VERSION) return unusable(SduiDiagnostic.Code.UnsupportedVersion)
            val sections = root["sections"] as? JsonArray ?: return unusable(SduiDiagnostic.Code.InvalidDocument)

            val entries = mutableListOf<Entry>()
            val seen = mutableSetOf<String>()
            for (raw in sections) {
                val section = raw as? JsonObject
                val sectionId = section?.string("id")
                val type = section?.string("type")
                if (section == null || sectionId == null || type == null) {
                    entries += Entry.Rejected(SduiDiagnostic(SduiDiagnostic.Code.InvalidProps, sectionId = sectionId))
                    continue
                }
                if (!seen.add(sectionId)) {
                    entries += Entry.Rejected(SduiDiagnostic(SduiDiagnostic.Code.DuplicateId, sectionId = sectionId, sectionType = type))
                    continue
                }
                entries += read(section, sectionId, type)
            }
            return SduiDocument(id, schemaVersion, locale, screen, true, emptyList(), entries)
        }

        /** 섹션 하나를 읽어요. 못 읽었고 fallback을 써도 되면 fallback을 읽어요. */
        private fun read(section: JsonObject, sectionId: String, type: String): Entry {
            val diagnostics = mutableListOf<SduiDiagnostic>()
            var drawnType = type
            var outcome = props(type, section["props"], sectionId, diagnostics)
            var isFallback = false
            val fallback = section["fallback"] as? JsonObject
            val fallbackType = fallback?.string("type")
            if (outcome.props == null && outcome.mayFallBack && fallback != null && fallbackType != null) {
                outcome = props(fallbackType, fallback["props"], sectionId, diagnostics)
                drawnType = fallbackType
                isFallback = true
            }
            val area = section.string("area")
            val read = outcome.props?.let {
                SduiSection(sectionId, drawnType, type, area, it, logging(section["logging"]), isFallback)
            }
            return Entry.Read(area, read, diagnostics)
        }

        /** props를 읽어요. 그릴 수 없으면 props가 null이고, fallback을 써도 되는지 같이 돌려줘요. */
        private fun props(type: String, props: JsonElement?, sectionId: String, diagnostics: MutableList<SduiDiagnostic>): Outcome {
            fun add(code: SduiDiagnostic.Code, field: String?) {
                diagnostics += SduiDiagnostic(code, sectionId, type, field)
            }
            val notes = mutableListOf<SduiPropsNote>()
            return try {
                val read = SduiSectionProps.read(type, props, notes)
                if (read == null) {
                    add(SduiDiagnostic.Code.UnknownType, null)
                    Outcome(null, mayFallBack = true)
                } else {
                    // 같은 종류의 진단은 한 번만, unknownEnum → unsupportedAction 순서로 남겨요.
                    notes.firstOrNull { it is SduiPropsNote.UnknownEnum }?.let { add(SduiDiagnostic.Code.UnknownEnum, it.field) }
                    notes.firstOrNull { it is SduiPropsNote.UnsupportedAction }?.let { add(SduiDiagnostic.Code.UnsupportedAction, it.field) }
                    Outcome(read, mayFallBack = false)
                }
            } catch (error: SduiPropsException) {
                if (error.kind == SduiPropsException.Kind.INVALID) {
                    add(SduiDiagnostic.Code.InvalidProps, error.field)
                    Outcome(null, mayFallBack = true)
                } else {
                    add(SduiDiagnostic.Code.EmptySection, error.field)
                    Outcome(null, mayFallBack = false)
                }
            } catch (error: RuntimeException) {
                // 읽다가 생각하지 못한 오류가 나도 앱은 죽지 않고 그 섹션만 빼요.
                add(SduiDiagnostic.Code.InvalidProps, null)
                Outcome(null, mayFallBack = true)
            }
        }

        /** "major.minor"에서 major를 읽어요. 숫자 두 덩어리가 아니면 null이에요. */
        internal fun majorVersion(version: String?): Int? {
            val parts = version?.split(".") ?: return null
            if (parts.size != 2 || parts.any { part -> part.isEmpty() || part.any { it !in '0'..'9' } }) return null
            return parts[0].toIntOrNull()
        }

        private fun screen(json: JsonElement?): SduiScreenInfo? {
            val screen = json as? JsonObject ?: return null
            val id = screen.string("id") ?: return null
            val revision = (screen["revision"] as? JsonPrimitive)?.wholeNumber() ?: return null
            if (revision !in Int.MIN_VALUE..Int.MAX_VALUE) return null
            return SduiScreenInfo(id, revision.toInt())
        }

        /** 섹션의 logging 값을 읽어요. 글자·숫자·참거짓만 받고 나머지는 버려요. 정수는 Long, 소수는 Double이에요. */
        private fun logging(json: JsonElement?): Map<String, Any> {
            val logging = json as? JsonObject ?: return emptyMap()
            val values = LinkedHashMap<String, Any>()
            for ((key, value) in logging) {
                val primitive = value as? JsonPrimitive ?: continue
                if (primitive is JsonNull) continue
                val flag = if (primitive.isString) null else primitive.booleanOrNull
                when {
                    primitive.isString -> values[key] = primitive.content
                    flag != null -> values[key] = flag
                    else -> {
                        val number = primitive.doubleOrNull ?: continue
                        values[key] = primitive.wholeNumber() ?: number
                    }
                }
            }
            return values
        }

        private fun JsonObject.string(key: String): String? =
            (this[key] as? JsonPrimitive)?.takeIf { it.isString }?.content

        /** 소수점 아래가 없는 숫자면 Long으로 읽어요(1.0도 1이에요). */
        private fun JsonPrimitive.wholeNumber(): Long? {
            if (isString || this is JsonNull) return null
            val number = doubleOrNull ?: return null
            if (!number.isFinite() || number != Math.rint(number) || kotlin.math.abs(number) > 9.007199254740992E15) return null
            return number.toLong()
        }
    }
}

/** 문서의 화면 정보예요. */
data class SduiScreenInfo(
    /** 화면 이름(예: popup_detail) */
    val id: String,
    /** 어느 발행본으로 만든 문서인지예요. 첫 출시는 0이에요. */
    val revision: Int,
)

/** 그릴 섹션 하나예요. */
data class SduiSection(
    /** 화면 안에서 유일한 섹션 id */
    val id: String,
    /** 그리는 섹션 type이에요. fallback을 그리면 fallback의 type이에요. */
    val type: String,
    /** 문서에 적힌 type이에요. */
    val sourceType: String,
    /** 네이티브 화면 안 자리예요. 본문 섹션은 null이에요. */
    val area: String?,
    /** 읽은 props */
    val props: SduiSectionProps,
    /** 로그에 더 붙일 값(문서의 logging)이에요. */
    val logging: Map<String, Any> = emptyMap(),
    /** fallback으로 그리는지예요. */
    val isFallback: Boolean = false,
)
