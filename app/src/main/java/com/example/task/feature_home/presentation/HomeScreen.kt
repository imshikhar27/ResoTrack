package com.example.task.feature_home.presentation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.task.feature_home.presentation.components.CommunityCard
import com.example.task.objects.AccountViewModelObject
import kotlinx.coroutines.launch
import android.content.Context
import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.example.task.objects.HomeViewModelObject

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    val userDetailsState = homeViewModel.userCommunityListState.value
    val userCommunitiesState = homeViewModel.userCommunitiesState.value
    val communityState = homeViewModel.communityState.value
    val _communityState = homeViewModel._communityState

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current // Access context for copying text

    LaunchedEffect(key1 = communityState) {
        val uid = AccountViewModelObject.accountViewModel.uid.value
        if (communityState is CommunityState.Changed || uid != userDetailsState.uid) {
            homeViewModel.onEvent(HomeEvent.SetUID(uid))
            homeViewModel.onEvent(HomeEvent.GetCommunityList)
            _communityState.value = CommunityState.Unchanged
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(userCommunitiesState.size) { communityIndex ->
                CommunityCard(
                    name = userCommunitiesState[communityIndex].name,
                    description = userCommunitiesState[communityIndex].description,
                    image = userCommunitiesState[communityIndex].image,
                    cid = userCommunitiesState[communityIndex].cid,
                    onClick = {
                        // Trigger chosenCommunity event with cid
                        HomeViewModelObject.homeViewModel.onEvent(HomeEvent.ChosenCommunity(userCommunitiesState[communityIndex].cid))
                        val cid = userCommunitiesState[communityIndex].cid
                        val name = userCommunitiesState[communityIndex].name
                        val image = userCommunitiesState[communityIndex].image
                        val members = userCommunitiesState[communityIndex].members
                        val membersString = members.joinToString(",")
                        navController.navigate("CommunityScreen?cid=$cid&name=$name&image=$image&members=$membersString")
                    },
                    onDelete = {
                        // Handle delete action here
                        HomeViewModelObject.homeViewModel.onEvent(HomeEvent.DeleteCommunity(userCommunitiesState[communityIndex].cid))
                    },
                    onShowCid = {
                        coroutineScope.launch {
                            val cid = userCommunitiesState[communityIndex].cid
                            snackbarHostState.showSnackbar(
                                message = "CID: $cid",
                                actionLabel = "Copy",
                                duration = SnackbarDuration.Short
                            ).also { result ->
                                if (result == SnackbarResult.ActionPerformed) {
                                    copyToClipboard(context, cid)
                                    Toast.makeText(context, "CID copied!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

// Helper function to copy text to clipboard
fun copyToClipboard(context: Context, text: String) {
    val clipboard = getSystemService(context, android.content.ClipboardManager::class.java)
    val clip = android.content.ClipData.newPlainText("CID", text)
    clipboard?.setPrimaryClip(clip)
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    BottomAppBar(
        containerColor = Color.White,  // Set the background color here
        contentColor = Color.Black,    // Set the color for the icons and text
        contentPadding = PaddingValues(0.dp)
    ) {
        // Profile Icon
        IconButton(onClick = {
            navController.navigate("ProfileScreen")
        }) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = {
            navController.navigate("CreateCommunityScreen")
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = { /* TODO: Add resolution settings navigation */ }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Resolution"
            )
        }
    }
}