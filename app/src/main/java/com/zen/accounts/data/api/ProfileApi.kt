package com.zen.accounts.data.api

import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.utility.Utility
import com.zen.accounts.presentation.utility.io
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ProfileApi @Inject constructor() {
    suspend fun updateProfilePic(user: User, profilePic : ByteArray) : Response<Unit> = suspendCoroutine { continuation ->
        val response = Response(value = Unit)
        val docRef = Utility.getUserDocRef(user.uid)

        val storageRef = Utility.getStorageRef(user.uid)
        val profileRef = storageRef.child("profile")

        io {
            try {
                val uploadTask = profileRef.putBytes(profilePic).await()
                if (uploadTask.task.isSuccessful) {
                    val imageDownloadUrl = uploadTask.storage.downloadUrl.await()
                    docRef.update("profilePicFirebaseFormat", imageDownloadUrl)
                        .addOnSuccessListener {
                            response.message = "Profile successfully uploaded into cloud."
                            response.status = true
                            continuation.resume(response)
                        }
                        .addOnFailureListener {
                            response.message = it.message.toString()
                            response.status = false
                            continuation.resume(response)
                        }
                } else {
                    throw  Exception("File did not upload to the cloud !")
                }
            } catch (e: Exception) {
                response.message = e.message.toString()
                response.status = false
                continuation.resume(response)
            }
        }
    }
}