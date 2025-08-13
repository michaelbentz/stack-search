package com.michaelbentz.stacksearch.data.remote.service

import com.michaelbentz.stacksearch.data.remote.model.QuestionSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionService {

    @GET("questions")
    suspend fun getLatestQuestions(
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity", // "creation"
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody",
        @Query("page") page: Int = 1,
    ): QuestionSearchResponse

    @GET("search/advanced")
    suspend fun searchQuestions(
        @Query("title") title: String,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody",
        @Query("page") page: Int = 1,
    ): QuestionSearchResponse
}
