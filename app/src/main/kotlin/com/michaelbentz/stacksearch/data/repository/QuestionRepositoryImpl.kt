package com.michaelbentz.stacksearch.data.repository

import com.michaelbentz.stacksearch.data.local.dao.QuestionDao
import com.michaelbentz.stacksearch.data.mapper.toDomain
import com.michaelbentz.stacksearch.data.mapper.toEntities
import com.michaelbentz.stacksearch.data.remote.service.QuestionService
import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionService: QuestionService,
    private val questionDao: QuestionDao,
) : QuestionRepository {

    override fun fetchActiveQuestions(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val response = questionService.getActiveQuestions()
            questionDao.replaceAll(response.toEntities())
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            emit(Resource.Error(ERROR_FETCH_LATEST_QUESTIONS, exception))
        }
    }

    override fun searchQuestions(query: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val response = questionService.searchQuestions(query)
            questionDao.replaceAll(response.toEntities())
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            emit(Resource.Error(ERROR_SEARCH_QUESTIONS, exception))
        }
    }

    override fun getQuestions(): Flow<List<Question>> {
        return questionDao
            .getAll()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getQuestionById(id: Long): Flow<Question?> {
        return questionDao
            .getById(id)
            .map { entity -> entity?.toDomain() }
    }

    companion object {
        private const val ERROR_FETCH_LATEST_QUESTIONS = "Failed to fetch latest questions"
        private const val ERROR_SEARCH_QUESTIONS = "Failed to load question search"
    }
}
