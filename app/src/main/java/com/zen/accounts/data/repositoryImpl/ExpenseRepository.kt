package com.zen.accounts.data.repositoryImpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.zen.accounts.data.api.ExpenseApi
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.db.dao.BackupTrackerDao
import com.zen.accounts.data.db.dao.ExpenseDao
import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.BackupTracker
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.presentation.utility.DateStringConverter
import com.zen.accounts.presentation.utility.io
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao : ExpenseDao,
    private val backupTrackerDao : BackupTrackerDao,
    private val expenseApi : ExpenseApi,
    val dataStore : UserDataStore
) {
    
    val allExpense : Flow<List<ExpenseWithOperation>> = expenseDao.getAllExpensesWithStatus()
    val expenses = Pager(
        PagingConfig(2)
    ){
        com.zen.accounts.data.api.paging.PagingSource()
    }.flow.cachedIn(CoroutineScope(Dispatchers.IO))
    
    val monthlyExpense : Flow<List<ExpenseWithOperation>> =
        expenseDao.getMonthlyExpensesWithStatus()
            .map {
                it.filter { expense ->
                    DateStringConverter().dateToString(expense.date).substring(
                        3,
                        6
                    ) == DateStringConverter().dateToString(Date(System.currentTimeMillis()))
                        .substring(3, 6)
                }
            }
    
    
    suspend fun insertExpenseIntoRoom(expense : Expense) {
        expenseDao.insertExpense(expense)
        backupTrackerDao.insertBackupTracker(
            BackupTracker(
                expenseId = expense.id,
                operation = "create",
                date = Date(System.currentTimeMillis())
            )
        )
    }
    
    suspend fun deleteExpenses(expenses : List<Long>) {
        io {
            expenseDao.deleteExpenses(expenses)
            // Store all expense's ids which are deleted because we need those id to delete from firebase.
            val tempList = arrayListOf<BackupTracker>()
            expenses.forEach {
                if (backupTrackerDao.getBackupTracker(it) == null) {
                    tempList.add(
                        BackupTracker(
                            expenseId = it,
                            operation = "delete",
                            date = Date(System.currentTimeMillis())
                        )
                    )
                } else {
                    backupTrackerDao.deleteRecord(it)
                }
            }
            backupTrackerDao.insertBackupTracker(tempList)
        }
    }
    
    suspend fun updateExpense(expense : Expense) {
        expenseDao.updateExpense(expense)
        if (backupTrackerDao.getBackupTracker(expense.id) != null) {
            backupTrackerDao.deleteRecord(expense.id)
        }
        backupTrackerDao.insertBackupTracker(
            BackupTracker(
                expenseId = expense.id,
                operation = "update",
                date = Date(System.currentTimeMillis())
            )
        )
    }
    
    suspend fun clearExpenseTable() {
        expenseDao.clearTable()
    }
    
    suspend fun getExpensesFromFirebase(uid : String) : Resource<Unit> {
        return withContext(Dispatchers.IO) {
            val res = expenseApi.getExpenseFromFirebase(uid)
            if (res.status) {
                expenseDao.insertExpenseList(res.value)
                Resource.SUCCESS(Unit)
            } else {
                Resource.FAILURE(res.message)
            }
        }
    }
    
    suspend fun clearBackupTable() {
        backupTrackerDao.deleteAll()
    }
    
    suspend fun isBackupTableEmpty() : Boolean {
        return backupTrackerDao.getTableEntryCount() == 0L
    }
    
}
