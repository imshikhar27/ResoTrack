package com.example.task.feature_resolution.domain

import android.net.Uri

data class Resolution(
    val resolutionText: String = "",
    val images: Uri? = null,
    val score: Int = 0,
    val peopleApproved: Int = 0
)
