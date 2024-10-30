package com.example.task.feature_account.domain.model

data class UserRetrieve(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profilePic: String? = null,
)
