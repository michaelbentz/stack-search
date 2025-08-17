package com.michaelbentz.stacksearch.presentation.component

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.michaelbentz.stacksearch.R

@Composable
fun RefreshErrorSnackbar(
    hasData: Boolean,
    isRefreshing: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val retryLabel = stringResource(R.string.action_retry)
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(hasData, isRefreshing, errorMessage) {
        if (hasData && !isRefreshing && !errorMessage.isNullOrBlank()) {
            val result = snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = retryLabel,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRetry()
            }
        }
    }
    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
    )
}
