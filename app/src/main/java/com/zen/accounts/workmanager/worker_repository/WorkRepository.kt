package com.zen.accounts.workmanager.worker_repository

import com.zen.accounts.api.ExpenseApi
import com.zen.accounts.api.resource.Resource
import com.zen.accounts.api.resource.Response
import com.zen.accounts.db.dao.BackupTrackerDao
import com.zen.accounts.db.dao.ExpenseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WorkRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val backupTrackerDao: BackupTrackerDao,
    private val expenseApi: ExpenseApi
) {
    suspend fun uploadExpenseToFirebase(uid : String) : Resource<Response<Unit>> {
        val expenseList = expenseDao.getCreatedExpenses()
        if(expenseList.isEmpty()) {
            return Resource.SUCCESS(Response(Unit, true, message = "Expense are already backup"))
        }
        return withContext(Dispatchers.IO) {
            val res = expenseApi.uploadToFirebase(uid, expenseList)
            if(res.status) Resource.SUCCESS(res)
            else Resource.FAILURE(res.message)
        }
    }

    suspend fun updateExpenseToFirebase(uid: String) : Resource<Response<Unit>> {
        val expenseList = expenseDao.getUpdatedExpenses()
        if(expenseList.isEmpty()) {
            return Resource.SUCCESS(Response(Unit, true, "no any updated expense was found."))
        }
        return withContext(Dispatchers.IO) {
            val res = expenseApi.uploadToFirebase(uid, expenseList)
            if(res.status) Resource.SUCCESS(res)
            else Resource.FAILURE(res.message)
        }
    }

    suspend fun deleteFromFirebase(uid: String) : Resource<Response<Unit>> {
        val expenseIds = backupTrackerDao.getDeletedExpensesId()
        if(expenseIds.isEmpty()) {
            return Resource.SUCCESS(Response(Unit, true, "no deleted expense was found"))
        }
        return withContext(Dispatchers.IO) {
            val res = expenseApi.deleteFromFirebase(uid, expenseIds)
            if(res.status) Resource.SUCCESS(res)
            else Resource.FAILURE(res.message)
        }
    }

    suspend fun clearCreatedExpenseFromBackupTable() {
        backupTrackerDao.deleteCreatedExpense()
    }

    suspend fun clearUpdatedExpenseFromBackupTable() {
        backupTrackerDao.deleteUpdatedExpense()
    }

    suspend fun clearDeletedExpenseFromBackupTable() {
        backupTrackerDao.deleteDeletedExpense()
    }
}