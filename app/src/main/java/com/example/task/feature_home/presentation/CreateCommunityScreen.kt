package com.example.task.feature_home.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.task.R

@Composable
fun CreateCommunityScreen(
    homeViewModel: HomeViewModel,
    navController: NavController
) {

    val userDetails = homeViewModel.userCommunityListState.value
    val createCommunitiesState = homeViewModel.createCommunitiesState.value
    val _createCommunitiesState = homeViewModel._createCommunitiesState

    val communityState = homeViewModel.communityState.value

    LaunchedEffect(communityState) {
        when(communityState){
            CommunityState.Changed -> {
                navController.navigate("HomeScreen?uid="+userDetails.uid)
                homeViewModel.onEvent(HomeEvent.ClearValues)
            }
            CommunityState.Loading -> Unit
            CommunityState.Unchanged -> homeViewModel.onEvent(HomeEvent.ClearValues)
        }
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            _createCommunitiesState.value = _createCommunitiesState.value.copy(
                image = uri
            )
        }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Join Community", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = createCommunitiesState.cid,
            onValueChange = {
                homeViewModel.onEvent(HomeEvent.EnteredCommunityCid(it))
            },
            label = { Text("Community ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            homeViewModel.onEvent(HomeEvent.JoinComminity)

        }) {
            Text(text = "Join Community")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Create Community", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberImagePainter(
                createCommunitiesState.image ?: R.drawable.baseline_account_circle_24
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .height(150.dp)
                .clickable {
                    launcher.launch("image/*")
                },
            contentDescription = "Picture",
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = createCommunitiesState.name,
            onValueChange = {
                homeViewModel.onEvent(HomeEvent.EnteredCommunityName(it))
            },
            label = { Text("Community Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = createCommunitiesState.description,
            onValueChange = {
                homeViewModel.onEvent(HomeEvent.EnteredCommunityDescription(it))
            },
            label = { Text("Community Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            homeViewModel.onEvent(HomeEvent.CreateCommunity)
        }) {
            Text(text = "Create")
        }
    }
}
