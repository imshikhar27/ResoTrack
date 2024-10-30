package com.example.task.feature_home.data

import android.net.Uri
import android.util.Log
import com.example.task.feature_home.domain.CommunityRetrieve
import com.example.task.feature_home.domain.UserCommunityList
import com.example.task.objects.FirebaseStorageObject
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class Firestore {

    val db = Firebase.firestore

    suspend fun addCommunityToUser(uid: String, community: String) {
        val userRef= db.collection("users").document(uid)
        val documentSnapshot = userRef.get().await()
        if(documentSnapshot.exists()){
            userRef.update("communities", FieldValue.arrayUnion(community))
                .await()
        }else{
            val hashmap=hashMapOf("communities" to arrayListOf(community))
            userRef.set(hashmap).await()
        }
    }

    suspend fun removeCommunityFromUser(uid: String, community: String) {
        val userRef= db.collection("users").document(uid)
        val documentSnapshot = userRef.get().await()
        if(documentSnapshot.exists()){
            userRef.update("communities", FieldValue.arrayRemove(community))
                .await()
        }
    }

    suspend fun getUserDetails(uid: String): UserCommunityList? {
        val docRef = db.collection("users").document(uid)
        val documentSnapshot = docRef.get().await()
        Log.d("Mytag", "getUserDetails: $documentSnapshot")
        return if (documentSnapshot.exists()) {
            UserCommunityList(
                uid = uid,
                communities = documentSnapshot.get("communities") as? List<String> ?: emptyList()
            )
        } else {
            null
        }
    }

    suspend fun addCommunityMember(cid: String, uid: String) {
        db.collection("communities").document(cid)
            .update("members", FieldValue.arrayUnion(uid))
            .await()
    }

    suspend fun removeCommunityMember(cid: String, uid: String) {
        db.collection("communities").document(cid)
            .update("members", FieldValue.arrayRemove(uid))
            .await()
    }

    suspend fun checkCommunityExist(cid: String): Boolean {
        val docRef = db.collection("communities").document(cid)
        val document = docRef.get().await()
        return document.exists()
    }

    suspend fun createCommunity(
        cid: String,
        uid: String,
        name: String,
        description: String,
        image: Uri?
    ) {
        if (image == null) return

        val imageUrl = FirebaseStorageObject.firebaseStorage.uploadPic(
            loc = "communityPic",
            filename = cid,
            uri = image
        )

        val community = hashMapOf(
            "name" to name,
            "description" to description,
            "image" to imageUrl,
            "members" to arrayListOf(uid)
        )

        db.collection("communities").document(cid).set(community).await()
    }

    suspend fun getCommunities(cid: String): CommunityRetrieve {
        val docRef = db.collection("communities").document(cid)
        val documentSnapshot = docRef.get().await()
        return if (documentSnapshot.exists()) {
            CommunityRetrieve(
                cid = cid,
                image = documentSnapshot.getString("image") ?: "",
                name = documentSnapshot.getString("name") ?: "",
                description = documentSnapshot.getString("description") ?: "",
                members = documentSnapshot.get("members") as? List<String> ?: emptyList()
            )
        } else {
            CommunityRetrieve(cid = cid)
        }
    }
}
