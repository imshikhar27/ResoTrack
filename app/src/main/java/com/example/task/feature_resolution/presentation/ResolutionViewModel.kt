package com.example.task.feature_resolution.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task.feature_resolution.domain.Resolution
import com.example.task.feature_resolution.domain.ResolutionRetrieve
import com.example.task.objects.ResolutionFireStoreObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ResolutionViewModel() : ViewModel() {
    private val _resolutionState = MutableStateFlow<List<ResolutionRetrieve>>(emptyList())
    val resolutionState: StateFlow<List<ResolutionRetrieve>> = _resolutionState

    fun startListeningForResolutions(cid: String, uidList: List<String>) {
        viewModelScope.launch {
            ResolutionFireStoreObject.resolutionFireStore.getResolution(cid, uidList)
                .collect { resolutions ->
                    _resolutionState.value = resolutions
                }
        }
    }

    fun event(event: ResolutionEvent) {
        when (event) {
            is ResolutionEvent.ResolutionScoreUpdate -> {
                viewModelScope.launch {
                    ResolutionFireStoreObject.resolutionFireStore.updateScore(
                        cid = event.cid,
                        uid = event.uid,
                        rid = event.rid,
                        score = event.score
                    )
                }
            }

            is ResolutionEvent.ResolutionCreate -> {
                viewModelScope.launch {
                    val rid = UUID.randomUUID().toString()
                    val resolution = Resolution(event.resolutionText, null, 0, 0)
                    ResolutionFireStoreObject.resolutionFireStore.addResolution(
                        cid = event.cid,
                        uid = event.uid,
                        rid = rid,
                        resolution = resolution
                    )
                }
            }

            is ResolutionEvent.ResolutionUpdateImage -> {
                viewModelScope.launch {
                    ResolutionFireStoreObject.resolutionFireStore.uploadImageInResolution(
                        rid = event.rid,
                        image = event.imageUri,
                        cid = event.cid,
                        uid = event.uid
                    )
                }
            }

            ResolutionEvent.GetResolutions -> TODO()
            ResolutionEvent.ViewStats -> TODO()
        }
    }
}