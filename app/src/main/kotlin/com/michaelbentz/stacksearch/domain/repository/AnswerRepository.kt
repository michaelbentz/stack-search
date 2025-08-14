package com.michaelbentz.stacksearch.domain.repository

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow

interface AnswerRepository {
    fun fetchAnswersByQuestionId(questionId: Long): Flow<Resource<Unit>>
    fun getAnswersByQuestionId(questionId: Long): Flow<List<Answer>>
}
