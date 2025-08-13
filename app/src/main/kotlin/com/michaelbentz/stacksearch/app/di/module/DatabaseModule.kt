package com.michaelbentz.stacksearch.app.di.module

import android.content.Context
import com.michaelbentz.stacksearch.data.local.Database
import com.michaelbentz.stacksearch.data.local.dao.QuestionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): Database {
        return Database.getInstance(appContext)
    }

    @Provides
    fun provideQuestionDao(database: Database): QuestionDao {
        return database.questionDao()
    }
}
