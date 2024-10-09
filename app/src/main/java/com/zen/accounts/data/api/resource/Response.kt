package com.zen.accounts.data.api.resource

data class Response<T> (
    var value : T,
    var status : Boolean = false,
    var message : String = ""
)