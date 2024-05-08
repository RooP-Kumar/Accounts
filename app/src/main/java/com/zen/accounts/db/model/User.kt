package com.zen.accounts.db.model

data class User(
    var uid : String = "",
    var name: String = "",
    var phone : String = "",
    var email : String = "",
    var isAuthenticated : Boolean = false
)
