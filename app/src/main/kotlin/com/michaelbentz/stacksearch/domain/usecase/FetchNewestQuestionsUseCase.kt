package com.michaelbentz.stacksearch.domain.usecase

import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import com.michaelbentz.stacksearch.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNewestQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
) {
    operator fun invoke(): Flow<Resource<Unit>> {
        return questionRepository.fetchNewestQuestions()
    }
}
