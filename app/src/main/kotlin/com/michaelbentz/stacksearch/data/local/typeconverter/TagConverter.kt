package com.michaelbentz.stacksearch.data.local.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagConverter {

    private val gson = Gson()
    private val listType = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return if (list.isNullOrEmpty()) "[]" else gson.toJson(list, listType)
    }

    @TypeConverter
    fun toList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            gson.fromJson<List<String>>(json, listType)
        }.getOrElse { emptyList() }
    }
}
