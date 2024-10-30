package com.example.task.feature_account.presentation

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.task.R
import com.example.task.feature_account.presentation.util.AccountEvent

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    accountViewModel: AccountViewModel
) {

    val accountState = accountViewModel.authState
    val _userState = accountViewModel._userState
    val userState = accountViewModel.userState
    val uid = accountViewModel.uid

    LaunchedEffect(accountState.value) {
        when (accountState.value) {
            is AuthState.Unauthenticated -> accountViewModel.onEvent(AccountEvent.ClearValues)
            is AuthState.Authenticated -> navController.navigate("HomeScreen?uid="+uid.value)
            else -> Unit
        }
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            _userState.value = userState.value.copy(
                profilePic = uri
            )
        }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Signup Page", fontSize = 32.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = rememberImagePainter(
                if (userState.value.profilePic != null) {
                    userState.value.profilePic
                } else {
                    R.drawable.baseline_account_circle_24
                }
            ),
            modifier = Modifier
                .clip(CircleShape)
                .height(150.dp)
                .clickable {
                    launcher.launch("image/*")
                },
            contentDescription = "Picture",
            contentScale = ContentScale.Fit,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userState.value.name,
            onValueChange = {
                accountViewModel.onEvent(AccountEvent.EnteredName(it))
            },
            label = {
                Text(text = "Name")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userState.value.email,
            onValueChange = {
                accountViewModel.onEvent(AccountEvent.EnteredEmail(it))
            },
            label = {
                Text(text = "Email")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = userState.value.password,
            onValueChange = {
                accountViewModel.onEvent(AccountEvent.EnteredPassword(it))
            },
            label = {
                Text(text = "Password")
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            accountViewModel.onEvent(AccountEvent.CreateUser)
        }) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("LoginScreen")
        }) {
            Text(text = "Have an account, Login")
        }
    }
}