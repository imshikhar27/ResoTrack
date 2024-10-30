package com.example.task.feature_home.presentation

import com.example.task.feature_home.domain.CommunityRetrieve

sealed class HomeEvent {
    data class EnteredCommunityCid(val cid: String): HomeEvent()
    data class EnteredCommunityName(val name: String): HomeEvent()
    data class EnteredCommunityDescription(val description: String): HomeEvent()
    data class SetUID(val uid: String?): HomeEvent()
    data class ChosenCommunity(val communityCid: CommunityRetrieve): HomeEvent()
    data class DeleteCommunity(val communityCid: String): HomeEvent()
    object CreateCommunity: HomeEvent()
    object ClearValues: HomeEvent()
    object getCommunities: HomeEvent()
    object GetCommunityList: HomeEvent()
    object JoinComminity: HomeEvent()
}