package ua.polodarb.gmsflags.ui.screens.suggestionsScreen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ua.polodarb.gmsflags.R
import ua.polodarb.gmsflags.ui.dialogs.FlagReportDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionsScreen(
    onSettingsClick: () -> Unit,
    onPackagesClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val showDialog = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val expandedFab by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            Box(
                modifier = Modifier.offset(y = 12.dp)
            ) {
                ExtendedFloatingActionButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { /* do something */ },
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
        LazyColumn(
            contentPadding = it,
            state = listState
        ) {
            items(5) {
                Column {
                    ListItem(
                        headlineContent = { Text("The new UI of Google Files") },
                        supportingContent = { Text("Finder: Nikola Brown") },
                        trailingContent = {
                            Row {
                                var checked by rememberSaveable { mutableStateOf(false) } // todo: remove it
                                FilledTonalIconButton(
                                    onClick = { showDialog.value = true },
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.ic_report),
                                        contentDescription = "Localized description"
                                    )
                                }
                                Switch(
                                    checked = checked,
                                    onCheckedChange = { checked = it }
                                )
                            }
                        },
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.padding(44.dp))
            }
        }
        FlagReportDialog(
            showDialog.value,
            onDismiss = { showDialog.value = false }
        )
    }
}