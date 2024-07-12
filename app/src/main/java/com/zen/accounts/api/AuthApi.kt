package com.zen.accounts.api

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.zen.accounts.api.resource.Response
import com.zen.accounts.db.model.User
import com.zen.accounts.utility.io
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthApi @Inject constructor() {
    suspend fun registerUsingEmailAndPassword(
        uid: String,
        pass: String,
        user: User
    ): Response<String> = suspendCoroutine { continuation ->
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Users").document(uid)
        val auth = FirebaseAuth.getInstance()
        val res = Response(value = "")
        auth.createUserWithEmailAndPassword(user.email, pass)
            .addOnSuccessListener { authResult ->
                docRef.set(user.copy(isAuthenticated = false))
                    .addOnSuccessListener {
                        authResult.user?.sendEmailVerification()
                        res.status = true
                        res.message = "Verification code sent to you email."
                        res.value = uid
                        continuation.resume(res)
                    }
                    .addOnFailureListener {
                        res.status = false
                        res.message = it.message.toString()
                        continuation.resume(res)
                    }
            }
            .addOnFailureListener {
                res.status = false
                res.message = it.message.toString()
                continuation.resume(res)
            }
    }

    suspend fun loginUsingEmailAndPassword(
        email: String,
        pass: String
    ): Response<Pair<User, ByteArray>> = suspendCoroutine { continuation ->
        val response = Response(value = Pair(User(), ByteArray(0)))
        val db = FirebaseFirestore.getInstance()
        val dbRef = Firebase.database.reference
        val auth = FirebaseAuth.getInstance()
        io {
            try {

                val authResult = auth.signInWithEmailAndPassword(email, pass).await()
                val uidMapResult =
                    db.collection("uidmap").document(authResult.user?.uid!!).get().await()
                if (!authResult.user?.isEmailVerified!!) {
                    response.status = false
                    response.message = "Please! Verify your email."
                    continuation.resume(response)
                } else {
                    val userUid = uidMapResult.get("uid").toString()
                    db.collection("Users").document(userUid)
                        .update("authenticated", true, "uid", userUid)
                    val userResult = db.collection("Users").document(userUid).get().await()
                    val retrievedUser = userResult.toObject(User::class.java)
                    retrievedUser?.let { retrievedUserFromCloud ->
                        retrievedUserFromCloud.profilePicFirebaseFormat?.let { url ->
                            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(url)
                            storageRef.getBytes(12527200L)
                                .addOnSuccessListener { bytes ->
                                    response.value = Pair(
                                        retrievedUser.copy(profilePicFirebaseFormat = null),
                                        bytes
                                    )
                                    response.status = true
                                    continuation.resume(response)
                                }
                                .addOnFailureListener {
                                    response.status = false
                                    response.message = it.message.toString()
                                    continuation.resume(response)
                                }
                        }

                    }
                }
            } catch (e: Exception) {
                response.status = false
                response.message = e.message.toString()
                continuation.resume(response)
            }
        }
    }


    fun logout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()
    }
}