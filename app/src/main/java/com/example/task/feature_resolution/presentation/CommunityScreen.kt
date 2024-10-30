package com.example.task.feature_resolution.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.task.feature_resolution.presentation.components.ResolutionCard
import com.example.task.objects.AccountViewModelObject
import com.example.task.objects.HomeViewModelObject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    resolutionViewModel: ResolutionViewModel,
    cid: String,
    name: String,
    image: String,
    members: List<String>) {

    val uid = AccountViewModelObject.accountViewModel.uid.value


    // Start listening for resolution changes when the page loads
    LaunchedEffect(Unit) {
        resolutionViewModel.startListeningForResolutions(cid, members)
    }

    val resolutions by resolutionViewModel.resolutionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = image,
                            contentDescription = "Community Image",
                            modifier = Modifier.size(40.dp),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("StatsPage") }) {
                        Icon(
                            imageVector = Icons.Default.Assessment, // Replace with your stats icon
                            contentDescription = "Stats"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            Column {
                ExtendedFloatingActionButton(
                    onClick = { expanded = !expanded },
                    text = { Text("Options") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Expand Options") }
                )
                if (expanded) {
                    FloatingActionButton(
                        onClick = {
                            expanded = false
                            navController.navigate("UpdateResolution")
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Update Resolution")
                    }
                    FloatingActionButton(
                        onClick = {
                            expanded = false
                            navController.navigate("CreateResolution")
                        },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Make Resolution")
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(resolutions.size) { index ->
                val resolution = resolutions[index]

                ResolutionCard(resolution = resolution)

                Spacer(modifier = Modifier.height(8.dp))

                var showMenu by remember { mutableStateOf(false) }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showMenu = true }
                    )
                ) {
                    ResolutionCard(resolution = resolution)

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        (1..5).forEach { score ->
                            DropdownMenuItem(
                                text = { Text("Score: $score") },
                                onClick = {
                                    showMenu = false
                                    resolutionViewModel.event(ResolutionEvent.ResolutionScoreUpdate(
                                        cid = cid!!,
                                        uid = resolution.uid,
                                        rid = resolution.rid,
                                        score = score as Long))
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Score $score added to resolution!")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

