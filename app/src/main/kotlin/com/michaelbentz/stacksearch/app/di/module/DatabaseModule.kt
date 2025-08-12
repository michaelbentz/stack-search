package com.michaelbentz.stacksearch.app.di.module

import android.content.Context
import com.michaelbentz.stacksearch.data.local.Database
import com.michaelbentz.stacksearch.data.local.dao.PlaceholderDao
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
    fun providePlaceholderDatabase(@ApplicationContext appContext: Context): Database {
        return Database.getInstance(appContext)
    }

    @Provides
    fun providePlaceholderDao(database: Database): PlaceholderDao {
        return database.placeholderDao()
    }
}
