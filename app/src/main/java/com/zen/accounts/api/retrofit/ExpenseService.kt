package com.zen.accounts.api.retrofit

import com.zen.accounts.db.model.Expense
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ExpenseService {
    @GET("/expense")
    fun getAllExpense() : Call<List<Expense>>

    @POST("/expense")
    suspend fun uploadExpense(@Body expense: Expense): Response<Long>
}