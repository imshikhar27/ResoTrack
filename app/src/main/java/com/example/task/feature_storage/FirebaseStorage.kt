package com.example.task.feature_storage

import android.net.Uri
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class FirebaseStorage {

        private val storage = Firebase.storage
        private val storageRef = storage.reference
    suspend fun uploadPic(loc: String, filename: String, uri: Uri): String? {
        // Create a reference to the file
        val ref: StorageReference = storageRef.child("$loc/$filename.jpg")

        return try {
            // Upload the file and wait for it to complete
            ref.putFile(uri).await()

            // After uploading, get the download URL and await its completion
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Handle any errors
            Log.e("Mytag", "Error uploading picture: ${e.message}", e)
            null
        }
    }

}