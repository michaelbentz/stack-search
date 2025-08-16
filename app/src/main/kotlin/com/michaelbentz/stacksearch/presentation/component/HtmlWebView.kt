package com.michaelbentz.stacksearch.presentation.component

import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun HtmlWebView(
    html: String,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier
            .wrapContentHeight(),
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = false
                    domStorageEnabled = false
                    loadsImagesAutomatically = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                loadDataWithBaseURL(
                    null,
                    html,
                    "text/html",
                    "utf-8",
                    null,
                )
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(
                null,
                html,
                "text/html",
                "utf-8",
                null,
            )
        }
    )
}
