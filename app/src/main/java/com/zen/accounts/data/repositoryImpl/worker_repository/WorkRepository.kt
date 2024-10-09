package com.zen.accounts.data.repositoryImpl.worker_repository

import com.zen.accounts.data.api.ExpenseApi
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.dao.BackupTrackerDao
import com.zen.accounts.data.db.dao.ExpenseDao
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
            return Resource.SUCCESS(Response(Unit, true, message = "Expense are already backup."))
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
            return Resource.SUCCESS(Response(Unit, true, "Expense are already backup."))
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
            return Resource.SUCCESS(Response(Unit, true, "Expense are already backup."))
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