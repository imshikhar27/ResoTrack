package com.example.task.feature_account.data

import android.net.Uri
import com.example.task.feature_account.domain.model.User
import com.example.task.objects.FirebaseStorageObject
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAccountData {


    val auth = Firebase.auth

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun createUser(
        email: String,
        password: String,
    ): String? {
        var result = false

        val job = withContext(Dispatchers.IO) {
            val task = auth.createUserWithEmailAndPassword(email, password)
                .await()
            if (task.user != null) {
                result = true
            }
        }
        //Log.d("Mytag", "createdUser: ${auth.currentUser?.uid}")

        return if (result) auth.currentUser?.uid else null
    }

    suspend fun getUid(): String? {
        return auth.currentUser?.uid
    }

    suspend fun signInUser(email: String, password: String): String? {
        return try {

            auth.signInWithEmailAndPassword(email, password).await()


            //Log.d("Mytag", "signedInUser: ${auth.currentUser?.uid}")
            auth.currentUser?.uid
        } catch (e: Exception) {

            //Log.e("Mytag", "Error signing in: ${e.message}", e)
            null
        }
    }

    fun deleteUser(): Task<Void> {
        val user = auth.currentUser
        return user!!.delete()
    }

    fun signOut() {
        auth.signOut()
    }

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateUserProfilePic(uri: Uri) {
        val user = auth.currentUser ?: return

        withContext(Dispatchers.IO) {
            try {

                val filename: String = user.uid
                val url = FirebaseStorageObject.firebaseStorage.uploadPic(
                    loc = "profilePic",
                    filename = filename,
                    uri = uri
                )


                val profileUpdates = userProfileChangeRequest {
                    photoUri = Uri.parse(url)
                }


                user.updateProfile(profileUpdates).await()

                //Log.d("Mytag", "updateUserProfilePic: Profile picture updated successfully")
            } catch (e: Exception) {
                //Log.e("Mytag", "Error updating profile picture", e)
            }
        }
    }

    suspend fun viewUserDetails(): User {
        var userData = User()


        withContext(Dispatchers.IO) {
            val user = auth.currentUser
            user?.let {
                for (profile in it.providerData) {
                    userData = userData.copy(uid = profile.uid)
                    if (profile.displayName != null)
                        userData = userData.copy(name = profile.displayName!!)
                    if (profile.email != null)
                        userData = userData.copy(email = profile.email!!)
                    if (profile.photoUrl != null)
                        userData = userData.copy(profilePic = profile.photoUrl)
                }
            }
            //Log.d("Mytag", "viewUserDetails: $userData")
        }

        return userData
    }


    @OptIn(DelicateCoroutinesApi::class)
    suspend fun updateUserName(name: String) {
        val user = auth.currentUser ?: return


        withContext(Dispatchers.IO) {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            //Log.d("Mytag", "updateUserName: $name $user")

            try {

                user.updateProfile(profileUpdates).await()
                //Log.d("Mytag", "updateUserName1: $name $user")
            } catch (e: Exception) {
                //Log.e("Mytag", "Error updating user name", e)
            }
        }
    }

    fun updateEmail(email: String): Task<Void> {
        val user = auth.currentUser
        return user!!.verifyBeforeUpdateEmail(email)
    }

}