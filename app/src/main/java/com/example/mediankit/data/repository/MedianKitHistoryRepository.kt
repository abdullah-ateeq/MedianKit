package com.example.mediankit.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.mediankit.model.MedianKitHistory

class MedianKitHistoryRepository(
    private val prefs: SharedPreferences
) {
    companion object {
        const val PREFS_NAME = "mediankit_history_prefs"
        const val KEY_HISTORY = "history_items"
        const val MAX_HISTORY_LIMIT = 10

        fun create(context: Context): MedianKitHistoryRepository {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return MedianKitHistoryRepository(prefs)
        }
    }

    fun getHistory(): List<MedianKitHistory> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return HistoryJsonCodec.deserializeList(json)
    }

    fun saveCalculation(item: MedianKitHistory): List<MedianKitHistory> {
        val current = getHistory().toMutableList()
        // Duplicate protection: if the most recent item has the exact same input values, avoid duplicate save
        if (current.isNotEmpty()) {
            val head = current.first()
            if (head.inputValues == item.inputValues && head.median == item.median && head.range == item.range) {
                return current
            }
        }
        current.add(0, item)
        // When adding the 11th, remove the oldest (keep max 10)
        val trimmed = if (current.size > MAX_HISTORY_LIMIT) {
            current.take(MAX_HISTORY_LIMIT)
        } else {
            current
        }
        val serialized = HistoryJsonCodec.serializeList(trimmed)
        prefs.edit().putString(KEY_HISTORY, serialized).apply()
        return trimmed
    }

    fun deleteCalculation(id: String): List<MedianKitHistory> {
        val current = getHistory().filterNot { it.id == id }
        val serialized = HistoryJsonCodec.serializeList(current)
        prefs.edit().putString(KEY_HISTORY, serialized).apply()
        return current
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
