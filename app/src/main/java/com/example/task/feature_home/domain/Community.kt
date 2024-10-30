package com.example.task.feature_home.domain

import android.net.Uri

data class Community(
    val cid: String = "",
    val image: Uri? = null,
    val name: String = "",
    val description: String = "",
    val members: List<String> = emptyList(),
)
