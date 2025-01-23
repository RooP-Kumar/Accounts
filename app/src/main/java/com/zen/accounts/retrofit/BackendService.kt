package com.zen.accounts.retrofit

import com.zen.accounts.data.db.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface BackendService {
    @POST("auth/signup")
    suspend fun signup(@Body user : User) : Map<String, String>
    
    @POST("/auth/login")
    suspend fun login(@Body user : User) : String
}
