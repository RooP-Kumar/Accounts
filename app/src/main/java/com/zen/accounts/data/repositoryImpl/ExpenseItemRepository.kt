package com.zen.accounts.data.repositoryImpl

import com.zen.accounts.data.db.dao.ExpenseItemDao
import com.zen.accounts.data.db.model.ExpenseItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExpenseItemRepository @Inject constructor(
    private val expenseItemDao: ExpenseItemDao
) {

    val allExpenseItem : Flow<List<ExpenseItem>> = expenseItemDao.getAllExpenseItems()


    suspend fun insertExpenseItemIntoRoom(expenseItem: ExpenseItem) {
        expenseItemDao.insertExpenseItem(expenseItem)
    }

    suspend fun updateExpenseItem(expenseItem: ExpenseItem) {
        expenseItemDao.updateExpenseItem(expenseItem)
    }

    suspend fun deleteExpenseItem(id: Long) {
        expenseItemDao.deleteExpenseItem(id)
    }

    suspend fun clearExpenseItemTable() {
        expenseItemDao.deleteAllItems()
    }
}