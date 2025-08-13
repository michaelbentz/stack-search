package com.michaelbentz.stacksearch.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.michaelbentz.stacksearch.data.local.dao.QuestionDao
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import com.michaelbentz.stacksearch.data.local.Database as StackSearchDatabase

@Database(
    entities = [
        QuestionEntity::class
    ],
    exportSchema = false,
    version = 1,
)
abstract class Database : RoomDatabase() {
    abstract fun questionDao(): QuestionDao

    companion object {
        @Volatile
        private var instance: StackSearchDatabase? = null

        fun getInstance(context: Context): StackSearchDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): StackSearchDatabase {
            return Room.databaseBuilder(
                context,
                StackSearchDatabase::class.java,
                "database",
            ).build()
        }
    }
}
