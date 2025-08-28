package project.stylo.common.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.ConditionalGenericConverter
import org.springframework.core.convert.converter.GenericConverter

class OptionMapListGenericConverter : ConditionalGenericConverter {
    private val objectMapper = ObjectMapper()
    private val listTypeRef = object : TypeReference<List<Map<String, String>>>() {}
    private val mapTypeRef = object : TypeReference<Map<String, String>>() {}

    override fun getConvertibleTypes(): MutableSet<GenericConverter.ConvertiblePair> {
        return mutableSetOf(
            GenericConverter.ConvertiblePair(String::class.java, List::class.java),
            GenericConverter.ConvertiblePair(String::class.java, MutableList::class.java)
        )
    }

    override fun matches(sourceType: TypeDescriptor, targetType: TypeDescriptor): Boolean {
        // Ensure target is a List whose element type is Map (of String,String if available)
        if (!List::class.java.isAssignableFrom(targetType.type)) return false
        val elementType = targetType.elementTypeDescriptor ?: return false
        if (!Map::class.java.isAssignableFrom(elementType.type)) return false
        // If generics are available, prefer String key/value, otherwise accept
        val keyDesc = elementType.mapKeyTypeDescriptor
        val valDesc = elementType.mapValueTypeDescriptor
        return (keyDesc == null || keyDesc.type == String::class.java) && (valDesc == null || valDesc.type == String::class.java)
    }

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        if (source == null) return null
        val str = source.toString().trim()
        if (str.isEmpty()) return emptyList<Map<String, String>>()
        // Try parsing as list
        try {
            return objectMapper.readValue(str, listTypeRef)
        } catch (_: Exception) {
            // Try parsing as single map and wrap into list
            try {
                val single: Map<String, String> = objectMapper.readValue(str, mapTypeRef)
                return listOf(single)
            } catch (_: Exception) {
                // As a last resort, return empty list to avoid binding crash
                return emptyList<Map<String, String>>()
            }
        }
    }
}
