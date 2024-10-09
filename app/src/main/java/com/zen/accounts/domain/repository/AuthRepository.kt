package com.zen.accounts.domain.repository

import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.User

interface AuthRepository {
    fun generateUID(user: User) : String

    suspend fun loginUser(email: String, pass: String): Resource<Response<User>>

    suspend fun uploadProfilePic(): Resource<Response<Unit>>

    suspend fun registerUser(user: User, pass: String) : Resource<Response<String>>

    suspend fun logout()
}