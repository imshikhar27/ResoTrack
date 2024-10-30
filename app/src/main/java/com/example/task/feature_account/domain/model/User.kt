package com.example.task.feature_account.domain.model

import android.net.Uri

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val profilePic: Uri? = null
)