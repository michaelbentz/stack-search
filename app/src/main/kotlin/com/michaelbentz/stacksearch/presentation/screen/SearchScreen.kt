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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.michaelbentz.stacksearch.R
import com.michaelbentz.stacksearch.presentation.component.SvgImage
import com.michaelbentz.stacksearch.presentation.model.QuestionUiData
import com.michaelbentz.stacksearch.presentation.state.SearchUiState
import com.michaelbentz.stacksearch.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val refreshError by viewModel.refreshError.collectAsStateWithLifecycle()

    val isRefreshing = (uiState as? SearchUiState.Data)?.isRefreshing == true
    val hasData = (uiState as? SearchUiState.Data)?.data?.questions?.isNotEmpty() == true
    val query = (uiState as? SearchUiState.Data)?.data?.query ?: ""

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
                                .height(48.dp),
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
                            .size(48.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
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
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                onQueryChange = viewModel::updateQuery,
                onSearch = viewModel::searchQuestions,
                query = query,
            )
            HorizontalDivider()
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
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                            Spacer(Modifier.height(12.dp))
                            FilledTonalButton(
                                onClick = viewModel::retryRefresh,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.action_retry),
                                )
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
    val focusManager = LocalFocusManager.current
    var value by remember(query) { mutableStateOf(query) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(value)
                    focusManager.clearFocus()
                }
            ),
            value = value,
            onValueChange = {
                value = it
                onQueryChange(it)
            },
            singleLine = true,
            placeholder = {
                Text(text = stringResource(R.string.search_placeholder))
            },
        )
    }
}

@Composable
private fun QuestionRow(
    question: QuestionUiData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (question.isAccepted) {
                Image(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.content_description_check),
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                text = stringResource(R.string.question_title, question.title),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                text = question.excerpt,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = stringResource(
                    R.string.question_asked_by,
                    question.askedDate,
                    question.owner
                ),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .width(72.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            LabeledValueItem(
                value = question.answers,
                label = stringResource(R.string.label_answers)
            )
            Spacer(Modifier.height(8.dp))
            LabeledValueItem(value = question.votes, label = stringResource(R.string.label_votes))
            Spacer(Modifier.height(8.dp))
            LabeledValueItem(value = question.views, label = stringResource(R.string.label_views))
        }
        Spacer(Modifier.width(8.dp))
        Image(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = stringResource(R.string.content_description_arrow),
        )
    }
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
            style = MaterialTheme.typography.labelMedium,
            text = "$value $label",
            maxLines = 1,
        )
    }
}
