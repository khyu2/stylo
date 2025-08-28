package project.stylo.common.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter

class OptionMapListConverter : Converter<String, List<Map<String, String>>> {
    private val objectMapper = ObjectMapper()
    private val typeRef = object : TypeReference<List<Map<String, String>>>() {}

    override fun convert(source: String): List<Map<String, String>> {
        val s = source.trim()
        if (s.isEmpty()) return emptyList()
        return try {
            objectMapper.readValue(s, typeRef)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
