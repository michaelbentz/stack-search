package com.michaelbentz.stacksearch.app.di.module

import com.michaelbentz.stacksearch.data.repository.QuestionRepositoryImpl
import com.michaelbentz.stacksearch.domain.repository.QuestionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    @Binds
    abstract fun bindSearchRepository(
        implementation: QuestionRepositoryImpl,
    ): QuestionRepository
}
