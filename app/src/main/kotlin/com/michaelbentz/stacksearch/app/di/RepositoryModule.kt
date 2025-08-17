package com.michaelbentz.stacksearch.app.di

import com.michaelbentz.stacksearch.data.repository.AnswerRepositoryImpl
import com.michaelbentz.stacksearch.data.repository.QuestionRepositoryImpl
import com.michaelbentz.stacksearch.domain.repository.AnswerRepository
import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        implementation: QuestionRepositoryImpl,
    ): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindAnswerRepository(
        implementation: AnswerRepositoryImpl,
    ): AnswerRepository
}
