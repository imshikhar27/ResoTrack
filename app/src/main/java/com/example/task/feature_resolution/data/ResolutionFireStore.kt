package com.example.task.feature_resolution.data

import android.net.Uri
import com.example.task.feature_resolution.domain.Resolution
import com.example.task.feature_resolution.domain.ResolutionRetrieve
import com.example.task.feature_resolution.domain.UserStat
import com.example.task.objects.FirebaseStorageObject
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class ResolutionFireStore {
    val db = Firebase.firestore

    suspend fun addResolution(resolution: Resolution, cid: String, uid: String, rid: String) {
        db.collection("resolutions").document(cid)
            .collection(uid).document(rid).set(resolution).await()
    }

    suspend fun uploadImageInResolution(image: Uri, cid: String, uid: String, rid: String) {
        val imageUrl = FirebaseStorageObject.firebaseStorage.uploadPic(
            loc = "ResolutionPic",
            filename = rid,
            uri = image
        )

        val docRef = db.collection("resolutions").document(cid).collection(uid).document(rid)
        docRef.update("images", imageUrl).await()
        //docRef.update("image", imageUrl).await()
    }

    suspend fun updateScore(cid: String, uid: String, rid: String, score: Long) {
        val docRef = db.collection("resolutions").document(cid).collection(uid).document(rid)

        docRef.update(
            mapOf(
                "score" to FieldValue.increment(score),
                "peopleApproved" to FieldValue.increment(1)
            )
        ).await()
    }

    suspend fun getStats(cid: String, uidList: List<String>): List<UserStat> {
        val userStatsList = mutableListOf<UserStat>()

        for (uid in uidList) {
            val resolutionDocs = db.collection("resolutions")
                .document(cid)
                .collection(uid)
                .get()
                .await()
                .documents

            var totalScore = 0.0
            var resolutionCount = 0

            for (doc in resolutionDocs) {
                val resolution = doc.toObject(Resolution::class.java)

                if (resolution != null && resolution.peopleApproved > 0) {
                    totalScore += resolution.score.toDouble() / resolution.peopleApproved
                    resolutionCount++
                }
            }

            // Calculate the user's rating
            val rating = if (resolutionCount > 0) totalScore / resolutionCount else 0.0

            // Add the user statistics to the list
            userStatsList.add(UserStat(uid = uid, rating = rating))
        }

        return userStatsList
    }

    fun getResolution(cid: String, uidList: List<String>): Flow<List<ResolutionRetrieve>> = callbackFlow {
        val listeners = mutableListOf<ListenerRegistration>()

        uidList.forEach { uid ->
            val listener = db.collection("resolutions")
                .document(cid)
                .collection(uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error) // Close the flow on error
                        return@addSnapshotListener
                    }

                    // Rebuild the list of resolutions from the snapshot
                    val resolutionList = snapshot?.documents?.mapNotNull { doc ->
                        val resolutionText = doc.getString("resolutionText") ?: ""
                        val images = doc.getString("images") ?: ""
                        val score = doc.getLong("score")?.toInt() ?: 0
                        val peopleApproved = doc.getLong("peopleApproved")?.toInt() ?: 0

                        // Create `ResolutionRetrieve` instance
                        ResolutionRetrieve(
                            rid = doc.id,
                            uid = uid,
                            resolutionText = resolutionText,
                            images = images,
                            score = score,
                            peopleApproved = peopleApproved
                        )
                    } ?: emptyList()

                    // Send the updated list to the flow if the channel is open
                    if (!isClosedForSend) trySend(resolutionList)
                }

            listeners.add(listener)
        }

        awaitClose {
            listeners.forEach { it.remove() } // Clean up listeners when flow is closed
        }
    }


}