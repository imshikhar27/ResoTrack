package com.example.task.feature_account.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.task.feature_account.presentation.util.AccountEvent

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    accountViewModel: AccountViewModel
){

    val accountState = accountViewModel.authState
    val userState = accountViewModel.userState
    val uid = accountViewModel.uid

    LaunchedEffect(true) {
        accountViewModel.onEvent(AccountEvent.CheckLoginStatus)
        if(accountState.value == AuthState.Authenticated)
            navController.navigate("HomeScreen?uid="+uid.value)
    }

    LaunchedEffect(accountState.value) {
        when(accountState.value){
            is AuthState.Unauthenticated -> accountViewModel.onEvent(AccountEvent.ClearValues)
            is AuthState.Authenticated -> navController.navigate("HomeScreen?uid="+uid.value)
            else -> Unit
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login Page", fontSize = 32.sp)

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
            accountViewModel.onEvent(AccountEvent.SignInUser)
        }) {
            Text(text = "Login")
        }


        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("SignUpScreen")
        }) {
            Text(text = "Don't have an account, Signup")
        }

    }
}