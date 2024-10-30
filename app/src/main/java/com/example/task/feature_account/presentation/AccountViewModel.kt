package com.example.task.feature_account.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task.feature_account.data.FirebaseAccountData
import com.example.task.feature_account.domain.model.User
import com.example.task.feature_account.presentation.util.AccountEvent
import com.example.task.objects.FirebaseAccountDataObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AccountViewModel() : ViewModel() {

    val _userState = mutableStateOf(User())
    val userState: State<User> = _userState

    val _authState = mutableStateOf<AuthState>(AuthState.Unauthenticated)
    val authState: State<AuthState> = _authState

    val _uid = mutableStateOf<String?>(null)
    val uid: State<String?> = _uid


    fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.EnteredName -> {
                _userState.value = userState.value.copy(
                    name = event.name
                )
            }

            is AccountEvent.EnteredEmail -> {
                _userState.value = userState.value.copy(
                    email = event.email
                )
            }

            is AccountEvent.EnteredPassword -> {
                _userState.value = userState.value.copy(
                    password = event.password
                )
            }

            is AccountEvent.CreateUser -> {
                if (!(userState.value.email.isEmpty() || userState.value.password.isEmpty()
                            || userState.value.name.isEmpty() || userState.value.profilePic == null)
                ) {
                    // Toast.makeText(applicationContext, "Creating User", Toast.LENGTH_SHORT).show()
                    viewModelScope.launch {
                        _authState.value = AuthState.Loading

                        val result = FirebaseAccountDataObject.firebaseAccountData.createUser(
                            email = userState.value.email,
                            password = userState.value.password,
                        )

                        if (result != null) {
                            coroutineScope {

                                val updateNameTask = async {
                                    if (userState.value.name.isNotEmpty()) {
                                        FirebaseAccountDataObject.firebaseAccountData.updateUserName(
                                            userState.value.name
                                        )
                                    }
                                }

                                val updateProfilePicTask = async {
                                    if (userState.value.profilePic != null) {
                                        FirebaseAccountDataObject.firebaseAccountData.updateUserProfilePic(
                                            userState.value.profilePic!!
                                        )
                                    }
                                }


                                updateNameTask.await()
                                updateProfilePicTask.await()
                            }


                            _uid.value = result
                            _authState.value = AuthState.Authenticated
                        } else {
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                }
            }

            is AccountEvent.SignInUser -> {
                if (!(userState.value.email.isEmpty() || userState.value.password.isEmpty())) {

                    viewModelScope.launch {
                        _authState.value = AuthState.Loading
                        val result = FirebaseAccountDataObject.firebaseAccountData.signInUser(
                            email = userState.value.email,
                            password = userState.value.password
                        )

                        if (result != null) {
                            _uid.value = result
                            _authState.value = AuthState.Authenticated
                        } else {
                            _authState.value = AuthState.Unauthenticated
                        }

                    }


                }
            }

            is AccountEvent.UpdateUserName -> {
                TODO()

            }

            is AccountEvent.UpdateEmail -> {
                TODO()
            }

            is AccountEvent.DeleteUser -> {

                _authState.value = AuthState.Loading
                val result = FirebaseAccountDataObject.firebaseAccountData.deleteUser()
                if (result.isSuccessful) {
                    _authState.value = AuthState.Unauthenticated
                }

            }

            is AccountEvent.SignOut -> {

                _authState.value = AuthState.Loading
                FirebaseAccountDataObject.firebaseAccountData.signOut()
                _uid.value = null
                _authState.value = AuthState.Unauthenticated

            }

            is AccountEvent.ClearValues -> {
                _userState.value = User()
            }

            AccountEvent.PickImage -> TODO()
            AccountEvent.ViewUserDetails -> {
                viewModelScope.launch {
                    _userState.value =
                        FirebaseAccountDataObject.firebaseAccountData.viewUserDetails()
                }
            }

            AccountEvent.CheckLoginStatus -> {
                _authState.value = AuthState.Loading
                if (FirebaseAccountDataObject.firebaseAccountData.auth.currentUser != null) {
                    viewModelScope.launch {
                        _uid.value =
                            FirebaseAccountDataObject.firebaseAccountData.getUid()
                    }
                    _authState.value = AuthState.Authenticated
                } else
                    _authState.value = AuthState.Unauthenticated
            }
        }
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
}