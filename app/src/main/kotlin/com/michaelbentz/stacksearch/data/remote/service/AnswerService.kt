package com.michaelbentz.stacksearch.data.remote.service

import com.michaelbentz.stacksearch.data.remote.model.AnswersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnswerService {

    @GET("questions/{questionId}/answers")
    suspend fun getAnswersForQuestion(
        @Path("questionId") questionId: Long,
        @Query("pagesize") pageSize: Int = 20,
        @Query("order") order: String = "desc",
        @Query("sort") sort: String = "activity",
        @Query("site") site: String = "stackoverflow",
        @Query("filter") filter: String = "withbody",
        @Query("page") page: Int = 1,
    ): AnswersResponse
}
