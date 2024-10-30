package com.example.task.feature_resolution.presentation

import android.net.Uri

sealed class ResolutionEvent {
    data class ResolutionScoreUpdate(val cid: String,val uid: String,val rid: String, val score: Long) : ResolutionEvent()
    data class ResolutionCreate(val cid: String,val uid: String, val resolutionText: String) : ResolutionEvent()
    data class ResolutionUpdateImage(val cid: String,val uid: String,val rid: String, val imageUri: Uri) : ResolutionEvent()

    object GetResolutions : ResolutionEvent()
    object ViewStats : ResolutionEvent()
}