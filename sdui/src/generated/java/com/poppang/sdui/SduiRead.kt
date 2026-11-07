// 생성기가 만든 파일이에요. 직접 고치지 말고 PopPang-SDUI-Spec의 schema/·components/를 고쳐요.
package com.poppang.sdui

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull

/** 섹션 props를 읽다가 섹션을 그릴 수 없을 때 던져요. */
class SduiPropsException(val kind: Kind, val field: String) : Exception("$kind: $field") {
    enum class Kind {
        /** 필수 값이 없거나 비어 있어요. 진단 코드 emptySection */
        EMPTY,

        /** 값의 타입·모양·한도가 계약과 달라요. 진단 코드 invalidProps */
        INVALID,
    }
}

/** 섹션은 그리지만 남길 진단이에요. */
sealed interface SduiPropsNote {
    val field: String

    /** 모르는 enum 값이라 기본값으로 그려요. 진단 코드 unknownEnum */
    data class UnknownEnum(override val field: String) : SduiPropsNote

    /** 모르는 액션이나 route라 누르면 아무 일도 하지 않아요. 진단 코드 unsupportedAction */
    data class UnsupportedAction(override val field: String) : SduiPropsNote
}

/** props를 읽는 도우미예요. 규칙은 Spec의 fixtures/README.md 해석 규칙과 같아요. */
class SduiPropsReader internal constructor(
    private val values: Map<String, JsonElement>,
    private val nested: Boolean,
) {
    private var invalidField: String? = null
    private var emptyField: String? = null
    private val collected = mutableListOf<SduiPropsNote>()

    /** 그리되 남길 진단이에요. */
    val notes: List<SduiPropsNote> get() = collected

    companion object {
        /** props 값으로 읽기를 시작해요. props가 객체가 아니면 그 자체가 계약 위반이에요. */
        fun of(props: JsonElement?): SduiPropsReader = when (props) {
            null -> SduiPropsReader(emptyMap(), nested = false)
            is JsonObject -> SduiPropsReader(props, nested = false)
            else -> SduiPropsReader(emptyMap(), nested = false).also { it.markInvalid("props") }
        }
    }

    internal fun markInvalid(field: String) {
        if (invalidField == null) invalidField = field
    }

    internal fun markEmpty(field: String) {
        if (nested) markInvalid(field) else if (emptyField == null) emptyField = field
    }

    internal fun note(note: SduiPropsNote) {
        collected += note
    }

    internal fun merge(child: SduiPropsReader) {
        child.invalidField?.let { markInvalid(it) }
        child.emptyField?.let { markInvalid(it) }
        collected += child.collected
    }

    /** 읽은 결과를 해석 규칙의 우선순위로 정리해요. invalidProps가 emptySection보다 먼저예요. */
    fun finish() {
        invalidField?.let { throw SduiPropsException(SduiPropsException.Kind.INVALID, it) }
        emptyField?.let { throw SduiPropsException(SduiPropsException.Kind.EMPTY, it) }
    }

    private fun value(key: String, required: Boolean): JsonElement? {
        val value = values[key]
        if (value == null && required) markEmpty(key)
        return value
    }

    fun string(key: String, required: Boolean, minLength: Int?, maxLength: Int?, pattern: String?): String? {
        val value = value(key, required) ?: return null
        if (value !is JsonPrimitive || !value.isString) {
            markInvalid(key)
            return null
        }
        val text = value.content
        if (text.isEmpty() && minLength != null && minLength > 0) {
            if (required) markEmpty(key)
            return null
        }
        val length = text.codePointCount(0, text.length)
        if ((minLength != null && length < minLength) || (maxLength != null && length > maxLength)) {
            markInvalid(key)
            return null
        }
        if (pattern != null && !Regex(pattern).containsMatchIn(text)) {
            markInvalid(key)
            return null
        }
        return text
    }

    fun number(
        key: String,
        required: Boolean,
        integer: Boolean,
        minimum: Double?,
        maximum: Double?,
        exclusiveMinimum: Boolean,
        exclusiveMaximum: Boolean,
    ): Double? {
        val value = value(key, required) ?: return null
        val number = (value as? JsonPrimitive)
            ?.takeIf { !it.isString && it !is JsonNull && it.booleanOrNull == null }
            ?.doubleOrNull
        if (number == null || !number.isFinite() || (integer && number % 1.0 != 0.0)) {
            markInvalid(key)
            return null
        }
        if (minimum != null && (number < minimum || (exclusiveMinimum && number == minimum))) {
            markInvalid(key)
            return null
        }
        if (maximum != null && (number > maximum || (exclusiveMaximum && number == maximum))) {
            markInvalid(key)
            return null
        }
        return number
    }

    fun bool(key: String, required: Boolean): Boolean? {
        val value = value(key, required) ?: return null
        val flag = (value as? JsonPrimitive)?.takeIf { !it.isString }?.booleanOrNull
        if (flag == null) markInvalid(key)
        return flag
    }

    fun <E> enumValue(key: String, required: Boolean, fallback: E, lookup: (String) -> E?): E {
        val value = value(key, required) ?: return fallback
        if (value !is JsonPrimitive || !value.isString) {
            markInvalid(key)
            return fallback
        }
        val known = lookup(value.content)
        if (known == null) {
            note(SduiPropsNote.UnknownEnum(key))
            return fallback
        }
        return known
    }

    fun action(key: String, required: Boolean): SduiAction? {
        val value = value(key, required) ?: return null
        return SduiAction.read(value, key, this)
    }

    fun actions(key: String, required: Boolean, minItems: Int?, maxItems: Int?): List<SduiAction>? {
        val value = value(key, required) ?: return null
        if (value !is JsonArray) {
            markInvalid(key)
            return null
        }
        if (minItems != null && value.size < minItems) {
            if (value.isNotEmpty()) markInvalid(key) else if (required) markEmpty(key)
            return null
        }
        if (maxItems != null && value.size > maxItems) {
            markInvalid(key)
            return null
        }
        return value.map { SduiAction.read(it, key, this) }
    }
}
