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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.michaelbentz.stacksearch.R
import com.michaelbentz.stacksearch.presentation.component.SvgImage
import com.michaelbentz.stacksearch.presentation.model.QuestionItemUiData
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
    val refreshErrorMessage by viewModel.refreshErrorState.collectAsStateWithLifecycle()
    val query = (uiState as? SearchUiState.Data)?.data?.query ?: ""

    Scaffold(
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
                            // TODO: Wire up navigation drawer
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_menu),
                            contentDescription = stringResource(R.string.content_description_menu),
                        )
                    }
                },
                actions = { Spacer(Modifier.width(48.dp)) },
            )
        },
        modifier = modifier
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
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                        ) {
                            items(questionItems) { item ->
                                QuestionItemRow(
                                    item = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate(Screen.Detail.withArg(item.id))
                                        }
                                        .padding(12.dp),
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }

                is SearchUiState.Error -> Unit
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
    var value by remember(query) {
        mutableStateOf(query)
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            placeholder = {
                Text(text = stringResource(R.string.search_placeholder))
            },
            value = value,
            onValueChange = {
                value = it
                onQueryChange(it)
            },
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = {
                onSearch(value)
            }
        ) {
            Text(text = stringResource(R.string.action_search))
        }
    }
}

@Composable
private fun QuestionItemRow(
    item: QuestionItemUiData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(28.dp),
            painter = painterResource(id = R.drawable.ic_check),
            contentDescription = stringResource(R.string.content_description_check),
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                text = stringResource(R.string.question_title, item.title),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                style = MaterialTheme.typography.labelSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                text = item.excerpt,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = stringResource(R.string.question_asked_by, item.askedDate, item.owner),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .width(72.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            LabeledValueItem(value = item.answers, label = stringResource(R.string.label_answers))
            Spacer(Modifier.height(8.dp))
            LabeledValueItem(value = item.votes, label = stringResource(R.string.label_votes))
            Spacer(Modifier.height(8.dp))
            LabeledValueItem(value = item.views, label = stringResource(R.string.label_views))
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
