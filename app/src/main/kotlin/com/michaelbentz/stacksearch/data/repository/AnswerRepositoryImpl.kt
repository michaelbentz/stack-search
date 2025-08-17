package com.michaelbentz.stacksearch.data.repository

import com.michaelbentz.stacksearch.data.local.dao.AnswerDao
import com.michaelbentz.stacksearch.data.mapper.entity.toDomain
import com.michaelbentz.stacksearch.data.mapper.remote.toEntities
import com.michaelbentz.stacksearch.data.remote.service.AnswerService
import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.repository.AnswerRepository
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnswerRepositoryImpl @Inject constructor(
    private val answerService: AnswerService,
    private val answerDao: AnswerDao
) : AnswerRepository {

    override fun fetchAnswersByQuestionId(questionId: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val response = answerService.getAnswersForQuestion(questionId)
            answerDao.replaceForQuestion(questionId, response.toEntities())
            emit(Resource.Success(Unit))
        } catch (exception: Exception) {
            emit(Resource.Error(ERROR_FETCH_ANSWERS, exception))
        }
    }

    override fun getAnswersByQuestionId(questionId: Long): Flow<List<Answer>> {
        return answerDao
            .getByQuestionId(questionId)
            .map { list -> list.map { it.toDomain() } }
    }

    private companion object {
        const val ERROR_FETCH_ANSWERS = "Failed to load answers"
    }
}
