package com.zen.accounts.presentation.utility

import com.google.gson.Gson
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.ui.screens.common.BackupPlan

fun userToString(user : User) : String {
    val gson = Gson()
    return gson.toJson(user)
}

fun stringToUser(userString : String) : User {
    val gson = Gson()
    return gson.fromJson(userString, User::class.java)
}

fun backupPlanToString(backupPlan: BackupPlan) : String{
    return backupPlan.label
}

fun stringToBackupPlan(backPlanString: String) : BackupPlan {
    val backupPlan : BackupPlan? = BackupPlan.getAllBackupPlan().find {
        it.label == backPlanString
    }
    return backupPlan ?: BackupPlan.Off
}

fun expenseToString (data : Expense) : String {
    val gson = Gson()
    return gson.toJson(data)
}

fun stringToExpense(data: String) : Expense {
    val gson = Gson()
    return gson.fromJson(data, Expense::class.java)
}