package project.stylo.common.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper

class OptionMapListDeserializer : JsonDeserializer<List<Map<String, String>>>() {
    private val objectMapper = ObjectMapper()
    private val typeRef = object : TypeReference<List<Map<String, String>>>() {}

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<Map<String, String>> {
        val jsonString = p.text

        print("test jsonString: $jsonString")

        return try {
            objectMapper.readValue(jsonString, typeRef)
        } catch (e: Exception) {
            emptyList()
        }
    }
}