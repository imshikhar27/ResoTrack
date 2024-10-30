package com.example.task.feature_home.domain

data class UserCommunityList(
    val uid: String = "",
    val communities: List<String> = emptyList()
)