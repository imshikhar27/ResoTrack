package com.example.task.feature_home.domain

data class CommunityRetrieve(
    val cid: String = "",
    val image: String = "",
    val name: String = "",
    val description: String = "",
    val members: List<String> = emptyList(),
)
