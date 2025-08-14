package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.model.Answer
import com.michaelbentz.stacksearch.domain.repository.AnswerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnswersByQuestionIdUseCase @Inject constructor(
    private val answerRepository: AnswerRepository
) {
    operator fun invoke(questionId: Long): Flow<List<Answer>> {
        return answerRepository.getAnswersByQuestionId(questionId)
    }
}
