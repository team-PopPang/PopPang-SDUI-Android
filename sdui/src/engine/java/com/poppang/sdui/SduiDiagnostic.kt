// src/engine/ 파일은 Kotlin과 kotlinx.serialization만 써요. make test-kotlin이 Android 없이 이 파일로 픽스처를 돌려요.
package com.poppang.sdui

/** 문서를 읽다가 남기는 진단이에요. 화면을 멈추지 않고 로그로만 보내요. */
data class SduiDiagnostic(
    /** 진단 코드 */
    val code: Code,
    /** 진단이 생긴 섹션 id예요. 문서 전체에 대한 진단이나 id가 없는 섹션은 null이에요. */
    val sectionId: String? = null,
    /** 진단이 생긴 섹션 type이에요. fallback을 읽다가 생긴 진단은 fallback의 type이에요. */
    val sectionType: String? = null,
    /** 문제가 된 props 이름이에요(예: title). */
    val field: String? = null,
) {
    /** 진단 코드예요. Spec fixtures/README.md의 해석 규칙 표와 같아요. */
    enum class Code(val value: String) {
        /** schemaVersion의 major가 다르거나 읽을 수 없어요. 문서를 쓰지 않아요. */
        UnsupportedVersion("unsupportedVersion"),

        /** 문서가 객체가 아니거나 sections가 없어요. 아무것도 그리지 않아요. */
        InvalidDocument("invalidDocument"),

        /** 모르는 섹션 type이에요. fallback을 그리고, 없으면 건너뛰어요. */
        UnknownType("unknownType"),

        /** props가 계약과 달라요(타입, 모양, 한도, id·type 없음). fallback을 그리고, 없으면 건너뛰어요. */
        InvalidProps("invalidProps"),

        /** 모르는 enum 값이라 기본값으로 그려요. */
        UnknownEnum("unknownEnum"),

        /** 모르는 액션이나 route라 누르면 아무 일도 하지 않아요. */
        UnsupportedAction("unsupportedAction"),

        /** id가 겹쳐서 먼저 나온 섹션만 그려요. */
        DuplicateId("duplicateId"),

        /** 필수 값이 없거나 비어서 그 섹션을 그리지 않아요. fallback도 쓰지 않아요. */
        EmptySection("emptySection"),
    }
}
