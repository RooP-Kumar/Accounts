package com.zen.accounts.repository

import com.zen.accounts.api.ExpenseApi
import com.zen.accounts.api.resource.Resource
import com.zen.accounts.db.dao.BackupTrackerDao
import com.zen.accounts.db.dao.ExpenseDao
import com.zen.accounts.db.dao.ExpenseWithOperation
import com.zen.accounts.db.model.BackupTracker
import com.zen.accounts.db.model.Expense
import com.zen.accounts.utility.DateStringConverter
import com.zen.accounts.utility.io
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val backupTrackerDao: BackupTrackerDao,
    private val expenseApi: ExpenseApi
) : BaseRepository() {

    val allExpense : Flow<List<ExpenseWithOperation>> = expenseDao.getAllExpensesWithStatus()

    val monthlyExpense : Flow<List<ExpenseWithOperation>> = expenseDao.getMonthlyExpensesWithStatus(
        DateStringConverter().dateToString(Date(System.currentTimeMillis())).substring(3, 6)
    )
    suspend fun insertExpenseIntoRoom(expense: Expense) {
        expenseDao.insertExpense(expense)
        backupTrackerDao.insertBackupTracker(
            BackupTracker(
                expenseId = expense.id,
                operation = "create",
                date = Date(System.currentTimeMillis())
            )
        )
    }

    suspend fun deleteExpenses(expenses : List<Expense>) {
        io {
            expenseDao.deleteExpenses(expenses)
            // Store all expense's ids which are deleted because we need those id to delete from firebase.
            val tempList = arrayListOf<BackupTracker>()
            expenses.forEach {
                if(backupTrackerDao.getBackupTracker(it.id) == 0L) {
                    tempList.add(
                        BackupTracker(
                            expenseId = it.id,
                            operation = "delete",
                            date = Date(System.currentTimeMillis())
                        )
                    )
                } else {
                    backupTrackerDao.deleteRecord(it.id)
                }
            }
            backupTrackerDao.insertBackupTracker(tempList)
        }
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
        if(backupTrackerDao.getBackupTracker(expense.id) == 0L) {
            backupTrackerDao.insertBackupTracker(
                BackupTracker(
                    expenseId = expense.id,
                    operation = "update",
                    date = Date(System.currentTimeMillis())
                )
            )
        }
    }

    suspend fun clearExpenseTable() {
        expenseDao.clearTable()
    }

    suspend fun getExpensesFromFirebase(uid: String) : Resource<Unit> {
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