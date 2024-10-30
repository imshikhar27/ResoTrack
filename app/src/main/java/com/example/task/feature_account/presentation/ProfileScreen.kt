package com.example.task.feature_account.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.task.R
import com.example.task.feature_account.presentation.util.AccountEvent

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    accountViewModel: AccountViewModel
){

    val user = accountViewModel.userState.value
    val accountState = accountViewModel.authState

    LaunchedEffect(accountState.value) {
        when (accountState.value) {
            is AuthState.Unauthenticated -> navController.navigate("LoginScreen")
            else -> accountViewModel.onEvent(AccountEvent.ViewUserDetails)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Details", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = (
                    if(user.profilePic != null)
                        user.profilePic
                    else
                        R.drawable.baseline_account_circle_24),
            contentDescription = "Profile image",
            modifier = Modifier
                .clip(CircleShape)
                .height(150.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Name")
        Text(text = user.name)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Email")
        Text(text = user.email)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            accountViewModel.onEvent(AccountEvent.SignOut)
        }) {
            Text(text = "Sign Out")
        }

    }

}