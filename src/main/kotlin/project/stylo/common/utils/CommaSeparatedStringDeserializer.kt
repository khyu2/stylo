package project.stylo.common.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class CommaSeparatedStringDeserializer : JsonDeserializer<List<String>>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<String> {
        val value = p.valueAsString
        return if (value.isNullOrBlank()) {
            emptyList()
        } else {
            value.split(",").map { it.trim() }
        }
    }
}