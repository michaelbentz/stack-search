package com.michaelbentz.stacksearch.app.di.module

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.request().let { req ->
        val url = if (apiKey.isNotBlank() && req.url.queryParameter("key") == null) {
            req.url.newBuilder().addQueryParameter("key", apiKey).build()
        } else {
            req.url
        }
        chain.proceed(req.newBuilder().url(url).build())
    }
}
