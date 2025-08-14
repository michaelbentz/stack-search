package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.repository.AnswerRepository
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchAnswersByQuestionIdUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    operator fun invoke(questionId: Long): Flow<Resource<Unit>> {
        return answerRepository.fetchAnswersByQuestionId(questionId)
    }
}
