package com.michaelbentz.stacksearch.domain.repository

import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    fun fetchLatestQuestions(): Flow<Resource<Unit>>
    fun searchQuestions(query: String): Flow<Resource<Unit>>
    fun getQuestions(): Flow<List<Question>>
}
