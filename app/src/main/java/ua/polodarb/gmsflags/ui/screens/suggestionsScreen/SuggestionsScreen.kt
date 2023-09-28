package ua.polodarb.gmsflags.ui.screens.suggestionsScreen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import ua.polodarb.gmsflags.R
import ua.polodarb.gmsflags.ui.components.inserts.ErrorLoadScreen
import ua.polodarb.gmsflags.ui.components.inserts.LoadingProgressBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(
    isFirstStart: Boolean,
    onSettingsClick: () -> Unit,
    onPackagesClick: () -> Unit
) {
    val viewModel = koinViewModel<SuggestionScreenViewModel>()

    val overriddenFlags = viewModel.stateSuggestionsFlags.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    val showDialog = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 1 || listState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getAllOverriddenBoolFlags()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            Box(
                modifier = Modifier.offset(y = 12.dp)
            ) {
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
                    },
                    expanded = expandedFab,
                    icon = { Icon(painterResource(id = R.drawable.ic_question), "") },
                    text = { Text(text = "How can I suggest or report a flag?") },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Suggestions",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "Search", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Localized description"
                        )
                    }
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onPackagesClick()
                    }) {
                        Icon(
                            painterResource(id = R.drawable.ic_packages),
                            contentDescription = "Localized description"
                        )
                    }
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onSettingsClick()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            when (overriddenFlags.value) {
                is SuggestionsScreenUiStates.Success -> {

                    val data = (overriddenFlags.value as SuggestionsScreenUiStates.Success).data

                    LazyColumn(
                        state = listState
                    ) {
                        item {
                            WarningBanner(isFirstStart)
                        }
                        itemsIndexed(data.toList()) { index, item ->
                            SuggestedFlagItem(
                                flagName = item.flagName,
                                senderName = item.flagSender,
                                flagValue = item.flagValue,
                                flagOnCheckedChange = {
                                    viewModel.updateFlagValue(it, index)
                                    viewModel.overrideFlag(
                                        packageName = item.phenotypePackageName,
                                        name = item.phenotypeFlagName,
                                        boolVal = if (it) "1" else "0"
                                    )
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.padding(44.dp))
                        }
                    }
                }

                is SuggestionsScreenUiStates.Loading -> {
                    LoadingProgressBar()
                }

                is SuggestionsScreenUiStates.Error -> {
                    ErrorLoadScreen()
                }
            }
        }
//        FlagReportDialog(
//            showDialog.value,
//            onDismiss = { showDialog.value = false }
//        )
    }
}

@Composable
fun SuggestedFlagItem(
    flagName: String,
    senderName: String,
    flagValue: Boolean,
    flagOnCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(flagName) },
        supportingContent = { Text(stringResource(R.string.finder) + senderName) },
        trailingContent = {
            Row {
                Switch(
                    checked = flagValue,
                    onCheckedChange = {
                        flagOnCheckedChange(it)
                    },
                    enabled = true
                )
            }
        },
    )
}

@Composable
fun WarningBanner(
    isFirstStart: Boolean
) {
    var visibility by rememberSaveable {
        mutableStateOf(isFirstStart)
    }

    AnimatedVisibility(
        visible = visibility,
        enter = fadeIn(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_force_stop),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp)
                )
                Text(
                    text = "To apply the flag, you need to click \"Force stop\" several times in the settings on the *target* App info settings page.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { visibility = false },
                        modifier = Modifier.padding(8.dp),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                            disabledContainerColor = MaterialTheme.colorScheme.outline,
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}