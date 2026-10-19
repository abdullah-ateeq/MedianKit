package com.example.mediankit.data.repository

import com.example.mediankit.model.MedianKitHistory

object HistoryJsonCodec {
    private fun escape(s: String): String {
        return s.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    fun serialize(item: MedianKitHistory): String {
        val inputValuesStr = item.inputValues.joinToString(prefix = "[", postfix = "]") { "\"${escape(it)}\"" }
        val modesStr = item.modes.joinToString(prefix = "[", postfix = "]") { "\"${escape(it)}\"" }
        val freqStr = item.modeFrequency?.toString() ?: "null"

        return "{" +
                "\"id\":\"${escape(item.id)}\"," +
                "\"inputValues\":$inputValuesStr," +
                "\"count\":${item.count}," +
                "\"median\":\"${escape(item.median)}\"," +
                "\"modes\":$modesStr," +
                "\"modeFrequency\":$freqStr," +
                "\"minimum\":\"${escape(item.minimum)}\"," +
                "\"maximum\":\"${escape(item.maximum)}\"," +
                "\"range\":\"${escape(item.range)}\"," +
                "\"q1\":\"${escape(item.q1)}\"," +
                "\"q3\":\"${escape(item.q3)}\"," +
                "\"calculatedAt\":${item.calculatedAt}" +
                "}"
    }

    fun serializeList(list: List<MedianKitHistory>): String {
        return list.joinToString(prefix = "[", postfix = "]", separator = ",") { serialize(it) }
    }

    fun deserializeList(json: String): List<MedianKitHistory> {
        val trimmed = json.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()
        val content = trimmed.substring(1, trimmed.length - 1).trim()
        if (content.isEmpty()) return emptyList()

        val results = mutableListOf<MedianKitHistory>()
        var braceDepth = 0
        var inQuotes = false
        var isEscaped = false
        var startIndex = -1

        for (i in content.indices) {
            val c = content[i]
            if (isEscaped) {
                isEscaped = false
                continue
            }
            if (c == '\\') {
                isEscaped = true
                continue
            }
            if (c == '"') {
                inQuotes = !inQuotes
                continue
            }
            if (!inQuotes) {
                if (c == '{') {
                    if (braceDepth == 0) startIndex = i
                    braceDepth++
                } else if (c == '}') {
                    braceDepth--
                    if (braceDepth == 0 && startIndex != -1) {
                        val objStr = content.substring(startIndex, i + 1)
                        parseObject(objStr)?.let { results.add(it) }
                        startIndex = -1
                    }
                }
            }
        }
        return results
    }

    private fun parseObject(objStr: String): MedianKitHistory? {
        try {
            val id = extractStringField(objStr, "id") ?: return null
            val inputValues = extractStringList(objStr, "inputValues")
            val count = extractIntField(objStr, "count") ?: inputValues.size
            val median = extractStringField(objStr, "median") ?: "0"
            val modes = extractStringList(objStr, "modes")
            val modeFrequency = extractIntField(objStr, "modeFrequency")
            val minimum = extractStringField(objStr, "minimum") ?: "0"
            val maximum = extractStringField(objStr, "maximum") ?: "0"
            val range = extractStringField(objStr, "range") ?: "0"
            val q1 = extractStringField(objStr, "q1") ?: "0"
            val q3 = extractStringField(objStr, "q3") ?: "0"
            val calculatedAt = extractLongField(objStr, "calculatedAt") ?: System.currentTimeMillis()

            return MedianKitHistory(
                id = id,
                inputValues = inputValues,
                count = count,
                median = median,
                modes = modes,
                modeFrequency = modeFrequency,
                minimum = minimum,
                maximum = maximum,
                range = range,
                q1 = q1,
                q3 = q3,
                calculatedAt = calculatedAt
            )
        } catch (_: Exception) {
            return null
        }
    }

    private fun extractStringField(json: String, fieldName: String): String? {
        val pattern = Regex("\"$fieldName\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
        val match = pattern.find(json) ?: return null
        return unescape(match.groupValues[1])
    }

    private fun extractIntField(json: String, fieldName: String): Int? {
        val pattern = Regex("\"$fieldName\"\\s*:\\s*(-?\\d+)")
        val match = pattern.find(json) ?: return null
        return match.groupValues[1].toIntOrNull()
    }

    private fun extractLongField(json: String, fieldName: String): Long? {
        val pattern = Regex("\"$fieldName\"\\s*:\\s*(-?\\d+)")
        val match = pattern.find(json) ?: return null
        return match.groupValues[1].toLongOrNull()
    }

    private fun extractStringList(json: String, fieldName: String): List<String> {
        val pattern = Regex("\"$fieldName\"\\s*:\\s*\\[(.*?)\\]")
        val match = pattern.find(json) ?: return emptyList()
        val arrayContent = match.groupValues[1].trim()
        if (arrayContent.isEmpty()) return emptyList()

        val itemPattern = Regex("\"((?:\\\\.|[^\"\\\\])*)\"")
        return itemPattern.findAll(arrayContent).map { unescape(it.groupValues[1]) }.toList()
    }

    private fun unescape(s: String): String {
        return s.replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
    }
}
