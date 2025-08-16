package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.michaelbentz.stacksearch.R
import com.michaelbentz.stacksearch.presentation.component.HtmlWebView
import com.michaelbentz.stacksearch.presentation.model.AnswerSortOrder
import com.michaelbentz.stacksearch.presentation.model.AnswerUiData
import com.michaelbentz.stacksearch.presentation.model.DetailUiData
import com.michaelbentz.stacksearch.presentation.state.DetailUiState
import com.michaelbentz.stacksearch.presentation.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val refreshError by viewModel.refreshError.collectAsStateWithLifecycle()

    val isRefreshing = (uiState as? DetailUiState.Data)?.isRefreshing == true
    val hasData = uiState is DetailUiState.Data

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
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.content_description_back),
                        )
                    }
                },
                title = {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        text = stringResource(R.string.detail_title),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Error -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is DetailUiState.Data -> {
                with(state.data) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        item {
                            QuestionHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface),
                                data = this@with,
                            )
                        }
                        item {
                            AnswersHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                total = answers.size,
                                onSelect = viewModel::setSortOrder,
                                selected = sortOrder,
                            )
                            HorizontalDivider()
                        }
                        when {
                            isRefreshing -> {
                                item {
                                    Spacer(Modifier.height(16.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            answers.isNotEmpty() -> {
                                items(
                                    key = {
                                        it.id
                                    },
                                    items = answers,
                                ) { answer ->
                                    AnswerItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        answer = answer,
                                    )
                                    HorizontalDivider()
                                }
                                item {
                                    Spacer(Modifier.height(16.dp))
                                }
                            }

                            else -> {
                                item {
                                    Spacer(Modifier.height(8.dp))
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
private fun QuestionHeader(
    data: DetailUiData,
    modifier: Modifier = Modifier,
) {
    with(data) {
        val horizontalPadding = 16.dp
        Column(
            modifier = modifier,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
                style = MaterialTheme.typography.titleLarge,
                text = title,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_asked),
                    value = askedDate,
                )
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_modified),
                    value = modifiedDate,
                )
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_viewed),
                    value = views,
                )
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
            )
            Spacer(Modifier.height(4.dp))
            HtmlWebView(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                html = body,
            )
            Spacer(Modifier.height(8.dp))
            if (tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tags.forEach { tag ->
                        TagItem(tag)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            MetaStamp(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
                prefix = stringResource(R.string.meta_asked),
                dateText = askedExact,
            )
            Spacer(Modifier.height(8.dp))
            AuthorRowItem(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
                reputation = authorReputation,
                name = authorName,
                avatarUrl = authorAvatarUrl,
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MetaStamp(
    prefix: String,
    dateText: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodySmall.toSpanStyle()) {
                append("$prefix ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(dateText)
                }
            }
        }
    )
}

@Composable
private fun AnnotatedLabeledValueItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                append("$label ")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(value)
                }
            }
        },
    )
}

@Composable
private fun TagItem(
    text: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(8.dp),
        border = null,
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            text = text,
        )
    }
}

@Composable
private fun AuthorRowItem(
    name: String,
    reputation: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                model = avatarUrl,
                contentDescription = null,
            )
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                text = name,
            )
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(reputation)
                    }
                },
            )
        }
    }
}

@Composable
private fun AnswersHeader(
    total: Int,
    selected: AnswerSortOrder,
    onSelect: (AnswerSortOrder) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.answers_title, total),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.weight(1f))
        val labels = listOf(
            stringResource(R.string.sort_active),
            stringResource(R.string.sort_oldest),
            stringResource(R.string.sort_votes),
        )
        SegmentedTabs(
            labels = labels,
            selectedIndex = when (selected) {
                AnswerSortOrder.Active -> 0
                AnswerSortOrder.Oldest -> 1
                AnswerSortOrder.Votes -> 2
            },
            onSelect = { index ->
                onSelect(
                    when (index) {
                        0 -> AnswerSortOrder.Active
                        1 -> AnswerSortOrder.Oldest
                        else -> AnswerSortOrder.Votes
                    }
                )
            },
            enabled = total > 0,
        )
    }
}

@Composable
private fun SegmentedTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    height: Dp = 36.dp,
    horizontalPad: Dp = 10.dp,
    verticalPad: Dp = 6.dp,
) {
    val outline = MaterialTheme.colorScheme.outline
    val activeBg = MaterialTheme.colorScheme.surfaceVariant
    val inactiveText = MaterialTheme.colorScheme.onSurfaceVariant
    val activeText = MaterialTheme.colorScheme.onSurface

    val disabledAlpha = 0.38f
    val borderColor = if (enabled) {
        outline
    } else {
        outline.copy(alpha = disabledAlpha)
    }
    val textColorInactive = if (enabled) {
        inactiveText
    } else {
        inactiveText.copy(alpha = disabledAlpha)
    }
    Surface(
        modifier = modifier
            .then(
                if (!enabled) {
                    Modifier.semantics {
                        disabled()
                    }
                } else {
                    Modifier
                },
            ),
        border = BorderStroke(1.dp, borderColor),
        color = Color.Transparent,
        tonalElevation = 0.dp,
        shape = shape,
    ) {
        Row(
            Modifier
                .height(height)
                .clip(shape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val bg = when {
                    !enabled -> Color.Transparent
                    selected -> activeBg
                    else -> Color.Transparent
                }
                val labelColor = when {
                    !enabled -> textColorInactive
                    selected -> activeText
                    else -> inactiveText
                }
                val segmentBase = Modifier
                    .fillMaxHeight()
                    .background(bg)
                    .padding(horizontal = horizontalPad, vertical = verticalPad)

                val clickableMod = if (enabled) {
                    Modifier
                        .clickable(role = Role.Tab) { onSelect(index) }
                } else {
                    Modifier
                }
                Box(
                    modifier = segmentBase.then(clickableMod),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (enabled && selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = labelColor,
                    )
                }
                if (index != labels.lastIndex) {
                    Box(
                        Modifier
                            .padding(vertical = 1.dp)
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(borderColor),
                    )
                }
            }
        }
    }
}

@Composable
private fun AnswerItem(
    answer: AnswerUiData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .width(38.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(12.dp))
            Text(
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                text = answer.score,
            )
            Text(
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                text = answer.scoreText,
            )
            if (answer.isAccepted) {
                Spacer(Modifier.height(8.dp))
                Image(
                    modifier = Modifier
                        .size(34.dp),
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = stringResource(R.string.content_description_check),
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        val horizontalPadding = 8.dp
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            HtmlWebView(
                modifier = Modifier
                    .fillMaxWidth(),
                html = answer.body,
            )
            Spacer(Modifier.height(4.dp))
            MetaStamp(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
                prefix = stringResource(R.string.meta_answered),
                dateText = answer.created,
            )
            Spacer(Modifier.height(4.dp))
            AuthorRowItem(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding),
                reputation = answer.reputation,
                avatarUrl = answer.avatarUrl,
                name = answer.authorName,
            )
        }
    }
}
