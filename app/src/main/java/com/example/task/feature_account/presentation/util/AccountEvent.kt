package com.example.task.feature_account.presentation.util

sealed class AccountEvent {
    data class EnteredName(val name: String): AccountEvent()
    data class EnteredEmail(val email: String): AccountEvent()
    data class EnteredPassword(val password: String): AccountEvent()
    object PickImage: AccountEvent()
    object CreateUser: AccountEvent()
    object SignInUser: AccountEvent()
    object UpdateUserName: AccountEvent()
    object UpdateEmail: AccountEvent()
    object DeleteUser: AccountEvent()
    object SignOut: AccountEvent()
    object ClearValues: AccountEvent()
    object ViewUserDetails: AccountEvent()
    object CheckLoginStatus : AccountEvent()
}