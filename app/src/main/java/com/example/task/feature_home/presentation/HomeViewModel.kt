package com.example.task.feature_home.presentation

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task.feature_home.domain.Community
import com.example.task.feature_home.domain.CommunityRetrieve
import com.example.task.feature_home.domain.UserCommunityList
import com.example.task.objects.FirebaseAccountDataObject
import com.example.task.objects.FirestoreObject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel : ViewModel() {

    private val _userCommunityListState = mutableStateOf(UserCommunityList())
    val userCommunityListState: State<UserCommunityList> = _userCommunityListState

    val _createCommunitiesState = mutableStateOf(Community())
    val createCommunitiesState: State<Community> = _createCommunitiesState

    private val _userCommunitiesState = mutableStateOf(emptyList<CommunityRetrieve>())
    val userCommunitiesState: State<List<CommunityRetrieve>> = _userCommunitiesState

    val _communityState = mutableStateOf<CommunityState>(CommunityState.Changed)
    val communityState: State<CommunityState> = _communityState

    val _community = mutableStateOf<CommunityRetrieve?>(null)
    val community : State<CommunityRetrieve?> = _community

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.EnteredCommunityCid -> {
                _createCommunitiesState.value = createCommunitiesState.value.copy(
                    cid = event.cid
                )
            }

            is HomeEvent.EnteredCommunityName -> {
                _createCommunitiesState.value = createCommunitiesState.value.copy(
                    name = event.name
                )
            }

            is HomeEvent.EnteredCommunityDescription -> {
                _createCommunitiesState.value = createCommunitiesState.value.copy(
                    description = event.description
                )
            }

            is HomeEvent.CreateCommunity -> {
                val cid = UUID.randomUUID().toString()
                _createCommunitiesState.value = createCommunitiesState.value.copy(cid = cid)

                if (!(createCommunitiesState.value.cid.isEmpty() || createCommunitiesState.value.name.isEmpty()
                            || createCommunitiesState.value.description.isEmpty() || createCommunitiesState.value.image == null
                            || _communityState.value is CommunityState.Loading)
                ) {
                    viewModelScope.launch {
                        _communityState.value = CommunityState.Loading

                        val createCommunityDeferred = async {
                            FirestoreObject.firestore.createCommunity(
                                cid = createCommunitiesState.value.cid,
                                uid = userCommunityListState.value.uid,
                                name = createCommunitiesState.value.name,
                                description = createCommunitiesState.value.description,
                                image = createCommunitiesState.value.image!!
                            )
                        }

                        val addCommunityToUserDeferred = async {
                            FirestoreObject.firestore.addCommunityToUser(
                                uid = userCommunityListState.value.uid,
                                community = cid
                            )
                        }

                        awaitAll(createCommunityDeferred, addCommunityToUserDeferred)
                        _communityState.value = CommunityState.Changed
                    }
                }
            }

            is HomeEvent.ClearValues -> {
                _createCommunitiesState.value = Community()
            }

            is HomeEvent.getCommunities -> {
                if (createCommunitiesState.value.cid.isNotEmpty()) {
                    viewModelScope.launch {
                        val checkCommunityExistDeferred = async {
                            FirestoreObject.firestore.checkCommunityExist(createCommunitiesState.value.cid)
                        }

                        if (checkCommunityExistDeferred.await()) {
                            val addCommunityMemberDeferred = async {
                                FirestoreObject.firestore.addCommunityMember(
                                    createCommunitiesState.value.cid,
                                    userCommunityListState.value.uid
                                )
                            }

                            val addCommunityToUserDeferred = async {
                                FirestoreObject.firestore.addCommunityToUser(
                                    userCommunityListState.value.uid,
                                    createCommunitiesState.value.cid
                                )
                            }

                            awaitAll(addCommunityMemberDeferred, addCommunityToUserDeferred)
                        }
                    }
                }
            }

            is HomeEvent.SetUID -> {
                viewModelScope.launch {
                    val uid = event.uid
                    if (uid != null) {
                        _userCommunityListState.value = userCommunityListState.value.copy(
                            uid = uid
                        )
                    }
                }
            }

            HomeEvent.GetCommunityList -> {
                viewModelScope.launch {
                    val userDetailsDeferred = async {
                        FirestoreObject.firestore.getUserDetails(userCommunityListState.value.uid)
                    }

                    userDetailsDeferred.await()?.let { userDetails ->
                        _communityState.value = CommunityState.Loading
                        _userCommunityListState.value = userDetails
                        val communityListDeferred = userDetails.communities.map { cid ->
                            async { FirestoreObject.firestore.getCommunities(cid) }
                        }
                        _userCommunitiesState.value = communityListDeferred.awaitAll()
                        _communityState.value = CommunityState.Unchanged
                    }
                }
            }

            HomeEvent.JoinComminity -> {
                if (!(createCommunitiesState.value.cid.isEmpty() || _communityState.value is CommunityState.Loading)) {
                    viewModelScope.launch {
                        _communityState.value = CommunityState.Loading

                        val checkCommunityExistDeferred = async {
                            FirestoreObject.firestore.checkCommunityExist(createCommunitiesState.value.cid)
                        }

                        if (checkCommunityExistDeferred.await()) {
                            val addCommunityToUserDeferred = async {
                                FirestoreObject.firestore.addCommunityToUser(
                                    uid = userCommunityListState.value.uid,
                                    community = createCommunitiesState.value.cid
                                )
                            }

                            val addCommunityMemberDeferred = async {
                                FirestoreObject.firestore.addCommunityMember(
                                    uid = userCommunityListState.value.uid,
                                    cid = createCommunitiesState.value.cid
                                )
                            }

                            awaitAll(addCommunityToUserDeferred, addCommunityMemberDeferred)
                            _communityState.value = CommunityState.Changed
                        } else {
                            _communityState.value = CommunityState.Unchanged
                        }
                    }
                }
            }

            is HomeEvent.DeleteCommunity -> {
                _communityState.value = CommunityState.Loading
                val uid = _userCommunityListState.value.uid
                val communityCid = event.communityCid

                // Launch a coroutine scope for executing async tasks
                viewModelScope.launch {
                    // Parallel execution of network calls
                    val removeCommunityFromUserJob = async {
                        FirestoreObject.firestore.removeCommunityFromUser(uid, communityCid)
                    }
                    val removeCommunityMemberJob = async {
                        FirestoreObject.firestore.removeCommunityMember(communityCid, uid)
                    }
                    // Await completion of both network calls
                    try {
                        removeCommunityFromUserJob.await()
                        removeCommunityMemberJob.await()

                        // Update state to changed after successful deletion
                        _communityState.value = CommunityState.Changed
                    } catch (e: Exception) {
                        // Handle errors and possibly revert state
                    }
                }
            }
            is HomeEvent.ChosenCommunity -> {
                _community.value = event.communityCid
            }
        }
    }
}

sealed class CommunityState {
    object Changed : CommunityState()
    object Unchanged : CommunityState()
    object Loading : CommunityState()
}
