package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.michaelbentz.stacksearch.R
import com.michaelbentz.stacksearch.presentation.component.SvgImage
import com.michaelbentz.stacksearch.presentation.model.QuestionUiData
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
import com.michaelbentz.stacksearch.presentation.theme.LocalDimens
import com.michaelbentz.stacksearch.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val dimens = LocalDimens.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshError by viewModel.refreshError.collectAsStateWithLifecycle()

    val isRefreshing = (uiState as? SearchUiState.Data)?.isRefreshing == true
    val hasData = (uiState as? SearchUiState.Data)?.data?.questions?.isNotEmpty() == true
    val query = (uiState as? SearchUiState.Data)?.data?.query ?: ""

    val pullState = rememberPullToRefreshState()
    var showSwipeIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            showSwipeIndicator = false
        }
    }

    val shouldShowSnackbar = hasData && !isRefreshing && !refreshError.isNullOrBlank()
    val retryActionLabel = stringResource(R.string.action_retry)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(shouldShowSnackbar, refreshError) {
        if (shouldShowSnackbar) {
            val result = snackbarHostState.showSnackbar(
                message = refreshError ?: "",
                actionLabel = retryActionLabel,
            )
            if (result == SnackbarResult.ActionPerformed) {
                showSwipeIndicator = false
                viewModel.retryRefresh()
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        SvgImage(
                            modifier = Modifier
                                .height(dimens.imageXLarge),
                            resourceId = R.raw.logo,
                            contentDescription = stringResource(R.string.content_description_logo),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            // TODO: Implement navigation drawer
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.content_description_menu),
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(dimens.imageXXLarge),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isRefreshing && !showSwipeIndicator) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(dimens.icon),
                                strokeWidth = dimens.strokeMedium,
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                onQueryChange = viewModel::updateQuery,
                onSearch = { query ->
                    showSwipeIndicator = false
                    viewModel.searchQuestions(query)
                },
                query = query,
            )
            HorizontalDivider()
            PullToRefreshBox(
                modifier = Modifier.fillMaxSize(),
                state = pullState,
                isRefreshing = isRefreshing && showSwipeIndicator,
                onRefresh = {
                    showSwipeIndicator = true
                    viewModel.retryRefresh()
                },
                indicator = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = dimens.spacingMedium),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        PullToRefreshDefaults.Indicator(
                            state = pullState,
                            isRefreshing = isRefreshing && showSwipeIndicator,
                            containerColor = MaterialTheme.colorScheme.primary,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            ) {
                when (val state = uiState) {
                    is SearchUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is SearchUiState.Data -> {
                        with(state.data) {
                            if (questions.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = if (state.data.query.isBlank()) {
                                            stringResource(R.string.empty_questions)
                                        } else {
                                            stringResource(
                                                R.string.empty_search_results,
                                                state.data.query,
                                            )
                                        },
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                ) {
                                    items(questions) { question ->
                                        QuestionRow(
                                            question = question,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    navController.navigate(
                                                        Screen.Detail.withArg(
                                                            question.id
                                                        )
                                                    )
                                                }
                                                .padding(
                                                    horizontal = dimens.spacingMedium,
                                                    vertical = dimens.spacingSmall,
                                                ),
                                        )
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }

                    is SearchUiState.Error -> {
                        Box(
                            modifier = modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    text = state.message,
                                )
                                Spacer(Modifier.height(dimens.spacingMedium))
                                FilledTonalButton(
                                    onClick = {
                                        showSwipeIndicator = false
                                        viewModel.retryRefresh()
                                    }
                                ) {
                                    Text(
                                        text = stringResource(R.string.action_retry),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val focusManager = LocalFocusManager.current
    var value by remember(query) { mutableStateOf(query) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = dimens.spacing5XLarge)
                .padding(
                    horizontal = dimens.spacingMedium,
                    vertical = dimens.spacingSmall,
                ),
            value = value,
            onValueChange = {
                value = it
                onQueryChange(it)
            },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = stringResource(R.string.action_search),
                )
            },
            placeholder = {
                Text(
                    style = MaterialTheme.typography.bodyLarge,
                    text = stringResource(R.string.search_placeholder),
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(value)
                    focusManager.clearFocus()
                }
            ),
            shape = RoundedCornerShape(dimens.radiusLarge),
            colors = TextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
            ),
        )
    }
}

@Composable
private fun QuestionRow(
    question: QuestionUiData,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(dimens.spacingXXLarge),
            contentAlignment = Alignment.Center,
        ) {
            if (question.isAccepted) {
                Image(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.content_description_check),
                )
            }
        }
        Spacer(Modifier.width(dimens.spacingSmall))
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Normal,
                overflow = TextOverflow.Ellipsis,
                text = stringResource(R.string.question_title, question.title),
                maxLines = 1,
            )
            Spacer(Modifier.height(dimens.spacingTiny))
            Text(
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Normal,
                text = question.excerpt,
                maxLines = 3,
            )
            Spacer(Modifier.height(dimens.spacingSmall))
            AskedByText(
                modifier = Modifier
                    .fillMaxWidth(),
                askedDate = question.askedDate,
                owner = question.owner,
            )
        }
        Spacer(Modifier.width(dimens.spacingSmall))
        Column(
            modifier = Modifier
                .width(dimens.spacing5XLarge),
            horizontalAlignment = Alignment.Start,
        ) {
            LabeledValueItem(
                value = question.answers,
                label = stringResource(R.string.label_answers),
            )
            Spacer(Modifier.height(dimens.spacingSmall))
            LabeledValueItem(
                value = question.votes,
                label = stringResource(R.string.label_votes),
            )
            Spacer(Modifier.height(dimens.spacingSmall))
            LabeledValueItem(
                value = question.views,
                label = stringResource(R.string.label_views),
            )
        }
        Spacer(Modifier.width(dimens.spacingTiny))
        Image(
            modifier = Modifier
                .size(dimens.image),
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = stringResource(R.string.content_description_arrow),
        )
    }
}

@Composable
fun AskedByText(
    askedDate: String,
    owner: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 2,
) {
    val text = stringResource(
        R.string.question_asked_by,
        askedDate,
        owner,
    )
    val linkColor = MaterialTheme.colorScheme.secondary
    val annotatedString = remember(text, owner, linkColor) {
        buildAnnotatedString {
            append(text)
            if (owner.isNotBlank()) {
                val start = text.lastIndexOf(owner)
                if (start >= 0) {
                    addStyle(
                        SpanStyle(color = linkColor),
                        start = start,
                        end = start + owner.length,
                    )
                }
            }
        }
    }
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines,
        text = annotatedString,
    )
}

@Composable
private fun LabeledValueItem(
    value: Int,
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            text = "$value $label",
            maxLines = 2,
        )
    }
}
