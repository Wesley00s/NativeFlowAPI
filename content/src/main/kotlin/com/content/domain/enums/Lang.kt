package com.content.domain.enums

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.OBJECT) 
@JsonDeserialize(using = LangDeserializer::class) 
enum class Lang(
    val whisperCode: String,
    val llmLabel: String
) {
    PT_BR("pt", "Brazilian Portuguese"),
    PT_PT("pt", "European Portuguese"),

    EN_US("en", "United States English"),
    EN_UK("en", "British English"),

    ES_LATAM("es", "Latin American Spanish"),
    ES_ES("es", "European Spanish"),

    FR_FR("fr", "French"),
    FR_CA("fr", "Canadian French"),

    GERMAN("de", "German"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    CHINESE("zh", "Simplified Chinese"),
    RUSSIAN("ru", "Russian"),
    KOREAN("ko", "Korean"),
    TURKISH("tr", "Turkish"),
    DUTCH("nl", "Dutch"),
    ARABIC("ar", "Arabic"),
    HINDI("hi", "Hindi");

    companion object {
        fun findMatch(whisperCode: String?, llmLabel: String?): Lang? {
            return entries.find { it.llmLabel.equals(llmLabel, ignoreCase = true) }
                ?: entries.find { it.whisperCode.equals(whisperCode, ignoreCase = true) }
        }
    }
}

class LangDeserializer : JsonDeserializer<Lang>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Lang {
        val node = p.codec.readTree<JsonNode>(p)
        if (node.isTextual) {
            val text = node.asText()

            try {
                return Lang.valueOf(text.uppercase())
            } catch (_: Exception) { }
            Lang.entries.find { it.whisperCode.equals(text, true) }?.let { return it }
            Lang.entries.find { it.llmLabel.equals(text, true) }?.let { return it }
        }

        if (node.isObject) {
            val whisperCode = node.get("whisperCode")?.asText()
            val llmLabel = node.get("llmLabel")?.asText()

            Lang.findMatch(whisperCode, llmLabel)?.let { return it }
        }
        return Lang.EN_US
    }
}