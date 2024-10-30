package com.example.task.feature_resolution.domain

data class ResolutionRetrieve(
    val rid: String = "",
    val uid: String = "",
    val resolutionText: String = "",
    val images: String = "",
    val score: Int = 0,
    val peopleApproved: Int = 0
)
