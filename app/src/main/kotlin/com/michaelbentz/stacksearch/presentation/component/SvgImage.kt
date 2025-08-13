package com.michaelbentz.stacksearch.presentation.component

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun SvgImage(
    @RawRes resourceId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(SvgDecoder.Factory()) }
            .build()
    }
    val model = remember(resourceId) {
        ImageRequest.Builder(context)
            .data("android.resource://${context.packageName}/$resourceId")
            .crossfade(true)
            .build()
    }
    AsyncImage(
        modifier = modifier,
        imageLoader = imageLoader,
        model = model,
        contentDescription = contentDescription,
    )
}
