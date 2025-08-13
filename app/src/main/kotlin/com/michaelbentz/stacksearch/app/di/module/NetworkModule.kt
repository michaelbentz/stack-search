package com.michaelbentz.stacksearch.app.di.module

import com.michaelbentz.stacksearch.BuildConfig
import com.michaelbentz.stacksearch.data.remote.service.QuestionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.STACK_EXCHANGE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideQuestionService(retrofit: Retrofit): QuestionService {
        return retrofit.create(QuestionService::class.java)
    }
}
