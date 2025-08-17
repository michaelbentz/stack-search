package com.michaelbentz.stacksearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.michaelbentz.stacksearch.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(items: List<QuestionEntity>)

    @Query("SELECT * FROM question ORDER BY lastActivityEpochSec DESC")
    fun getAll(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM question WHERE questionId = :id LIMIT 1")
    fun getById(id: Long): Flow<QuestionEntity?>

    @Query("DELETE FROM question")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<QuestionEntity>) {
        deleteAll()
        insertAll(items)
    }
}
