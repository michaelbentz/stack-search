package com.michaelbentz.stacksearch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.michaelbentz.stacksearch.data.local.entity.AnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnswerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AnswerEntity>)

    @Query(
        """
        SELECT * FROM answer
        WHERE questionId = :questionId
        ORDER BY isAccepted DESC, score DESC, creationDateEpochSec ASC
        """
    )
    fun getByQuestionId(questionId: Long): Flow<List<AnswerEntity>>

    @Query("DELETE FROM answer WHERE questionId = :questionId")
    suspend fun deleteByQuestionId(questionId: Long): Int

    @Transaction
    suspend fun replaceForQuestion(questionId: Long, items: List<AnswerEntity>) {
        deleteByQuestionId(questionId)
        if (items.isNotEmpty()) {
            insertAll(items)
        }
    }
}
