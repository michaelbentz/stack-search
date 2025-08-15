package com.michaelbentz.stacksearch.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.michaelbentz.stacksearch.presentation.mapper.stripHtml
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

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .height(56.dp),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .wrapContentSize(),
                            text = "More Info",
                        )
                    }

                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Error -> Unit
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
                                data = this@with,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp),
                            )
                            HorizontalDivider()
                        }
                        item {
                            AnswersHeader(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                total = answers.size,
                            )
                            HorizontalDivider()
                        }
                        items(answers) { answer ->
                            AnswerItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                data = answer,
                            )
                            HorizontalDivider()
                        }
                        item {
                            Spacer(Modifier.height(16.dp))
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
        Column(
            modifier = modifier,
        ) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = title,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                AnnotatedLabeledValueItem(label = "Asked", value = askedDate)
                AnnotatedLabeledValueItem(label = "Active", value = activeDate)
                AnnotatedLabeledValueItem(label = "Viewed", value = "$views times")
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            // TODO: Handle HTML
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = body.stripHtml().trim().take(340),
            )
            Spacer(Modifier.height(16.dp))
            if (tags.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    tags.forEach { tag ->
                        TagItem(tag)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(
                text = buildAnnotatedString {
                    withStyle(MaterialTheme.typography.bodySmall.toSpanStyle()) {
                        append("Asked ")
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append(askedDate)
                        }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            AuthorRowItem(
                name = authorName,
                reputation = authorReputation.formatThousands(),
                avatarUrl = authorAvatarUrl,
            )
        }
    }
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
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
    modifier: Modifier = Modifier,
) {
    var selected by remember { mutableStateOf(AnswerSort.Votes) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$total Answers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        SortChip("Active", selected == AnswerSort.Active) {
            selected = AnswerSort.Active
        }
        Spacer(Modifier.width(8.dp))
        SortChip("Oldest", selected == AnswerSort.Oldest) {
            selected = AnswerSort.Oldest
        }
        Spacer(Modifier.width(8.dp))
        SortChip("Votes", selected == AnswerSort.Votes) {
            selected = AnswerSort.Votes
        }
    }
}

@Composable
private fun SortChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilterChip(
        modifier = modifier,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        border = if (selected) {
            null
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        },
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
                text = label,
            )
        }
    )
}

@Composable
private fun AnswerItem(
    data: AnswerUiData,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier
                .width(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                text = data.score.toString(),
            )
            Text(
                style = MaterialTheme.typography.labelSmall,
                text = "Votes",
            )
            if (data.isAccepted) {
                Spacer(Modifier.height(6.dp))
                Icon(
                    modifier = Modifier
                        .size(18.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Accepted",
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 14,
                text = data.body.stripHtml().ifBlank { "" },
            )
            Spacer(Modifier.height(12.dp))
            Text(
                style = MaterialTheme.typography.bodySmall,
                text = "Answered ${data.created}",
            )
            Spacer(Modifier.height(6.dp))
            AuthorRowItem(
                name = data.author,
                reputation = data.reputation.formatThousands(),
                avatarUrl = null,
            )
        }
    }
}

private fun Int.formatThousands(): String = "%,d".format(this)

private enum class AnswerSort { Active, Oldest, Votes }
