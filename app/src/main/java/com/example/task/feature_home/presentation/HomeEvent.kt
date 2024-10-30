package com.example.task.feature_home.presentation

sealed class HomeEvent {
    data class EnteredCommunityCid(val cid: String): HomeEvent()
    data class EnteredCommunityName(val name: String): HomeEvent()
    data class EnteredCommunityDescription(val description: String): HomeEvent()
    data class SetUID(val uid: String?): HomeEvent()
    data class ChosenCommunity(val communityCid: String): HomeEvent()
    data class DeleteCommunity(val communityCid: String): HomeEvent()
    object CreateCommunity: HomeEvent()
    object ClearValues: HomeEvent()
    object getCommunities: HomeEvent()
    object GetCommunityList: HomeEvent()
    object JoinComminity: HomeEvent()
}