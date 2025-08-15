package com.michaelbentz.stacksearch.app.di.module

import com.michaelbentz.stacksearch.util.DetailUiDateTimeFormatter
import com.michaelbentz.stacksearch.util.SearchUiDateTimeFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PresentationModule {

    @Provides
    @Singleton
    @SearchUiDateTimeFormatter
    fun provideSearchUiDateTimeFormatter(): DateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d, ''yy")
        .withZone(ZoneId.systemDefault())

    @Provides
    @Singleton
    @DetailUiDateTimeFormatter
    fun provideDetailUiDateTimeFormatter(): DateTimeFormatter = DateTimeFormatter
        .ofPattern("MMM d, yyyy 'at' HH:mm")
        .withZone(ZoneId.systemDefault())
}
