package com.zen.accounts.data.db.model

import com.google.gson.annotations.SerializedName


data class User(
    var uid: String = "",
    var name: String = "",
    var phone: String = "",
    var email: String = "",
    
    @SerializedName("pass")
    var password: String = "",
    
    var isAuthenticated: Boolean = false,
    var profilePicFirebaseFormat: String? = null
)
