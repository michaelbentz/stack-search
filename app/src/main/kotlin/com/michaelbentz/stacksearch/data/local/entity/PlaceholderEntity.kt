package com.michaelbentz.stacksearch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placeholder")
class PlaceholderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = ID,
) {
    companion object {
        const val ID: Long = 0L
    }
}
