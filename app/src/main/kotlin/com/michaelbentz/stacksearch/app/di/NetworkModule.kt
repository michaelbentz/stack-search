package com.michaelbentz.stacksearch.app.di

import android.content.Context
import com.michaelbentz.stacksearch.BuildConfig
import com.michaelbentz.stacksearch.app.network.NetworkMonitor
import com.michaelbentz.stacksearch.data.remote.interceptor.ApiKeyInterceptor
import com.michaelbentz.stacksearch.data.remote.service.AnswerService
import com.michaelbentz.stacksearch.data.remote.service.QuestionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext appContext: Context,
    ): NetworkMonitor = NetworkMonitor(appContext)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val apiKeyInterceptor = ApiKeyInterceptor(BuildConfig.STACK_EXCHANGE_API_KEY)
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.STACK_EXCHANGE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideQuestionService(retrofit: Retrofit): QuestionService {
        return retrofit.create(QuestionService::class.java)
    }

    @Singleton
    @Provides
    fun provideAnswerService(retrofit: Retrofit): AnswerService {
        return retrofit.create(AnswerService::class.java)
    }
}
