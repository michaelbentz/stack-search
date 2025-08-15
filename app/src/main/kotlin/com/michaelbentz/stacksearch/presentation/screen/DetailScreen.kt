package com.michaelbentz.stacksearch.presentation.screen

import android.text.Html
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
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
                            contentDescription = stringResource(R.string.content_description_back),
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
                            text = stringResource(R.string.detail_title),
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
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_asked),
                    value = askedDate,
                )
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_active),
                    value = activeDate,
                )
                AnnotatedLabeledValueItem(
                    label = stringResource(R.string.label_viewed_times),
                    value = "$views times",
                )
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
            MetaStamp(
                prefix = stringResource(R.string.meta_asked),
                dateText = askedDate,
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
                text = name.decodeHtml(),
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
                AnswerSort.Active -> 0
                AnswerSort.Oldest -> 1
                AnswerSort.Votes -> 2
            },
            onSelect = { i ->
                selected = when (i) {
                    0 -> AnswerSort.Active
                    1 -> AnswerSort.Oldest
                    else -> AnswerSort.Votes
                }
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
    val borderColor = if (enabled) outline else outline.copy(alpha = disabledAlpha)
    val textColorInactive = if (enabled) inactiveText else inactiveText.copy(alpha = disabledAlpha)

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
                text = stringResource(R.string.label_votes_title),
            )
            if (data.isAccepted) {
                Spacer(Modifier.height(8.dp))
                Image(
                    modifier = Modifier
                        .size(28.dp),
                    painter = painterResource(id = R.drawable.ic_check),
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
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 14,
                text = data.body.stripHtml().ifBlank { "" },
            )
            Spacer(Modifier.height(12.dp))
            MetaStamp(
                prefix = stringResource(R.string.meta_answered),
                dateText = data.created,
            )
            Spacer(Modifier.height(6.dp))
            AuthorRowItem(
                name = data.author,
                reputation = data.reputation.formatThousands(),
                avatarUrl = data.avatarUrl,
            )
        }
    }
}

private fun Int.formatThousands(): String = "%,d".format(this)

private enum class AnswerSort { Active, Oldest, Votes }

fun String.decodeHtml(): String = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
