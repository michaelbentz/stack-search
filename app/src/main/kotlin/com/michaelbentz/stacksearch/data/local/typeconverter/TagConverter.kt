package com.michaelbentz.stacksearch.data.local.typeconverter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TagConverter {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromList(list: List<String>?): String {
        return if (list.isNullOrEmpty()) "[]" else gson.toJson(list, type)
    }

    @TypeConverter
    fun toList(json: String?): List<String> {
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, type)
    }
}
