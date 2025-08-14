package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.model.Question
import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionByIdUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(id: Long): Flow<Question?> {
        return questionRepository.getQuestionById(id)
    }
}
