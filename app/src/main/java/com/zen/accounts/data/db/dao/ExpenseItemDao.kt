package com.zen.accounts.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.zen.accounts.data.db.model.ExpenseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseItemDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExpenseItem(expenseItem : ExpenseItem)

    @Update
    suspend fun updateExpenseItem(expenseItem: ExpenseItem)

    @Query("DELETE FROM expense_items WHERE id = :id")
    suspend fun deleteExpenseItem(id : Long)

    @Query("SELECT * FROM expense_items WHERE id = :id")
    fun getExpenseItem(id : Long) : Flow<ExpenseItem>

    @Query("SELECT * FROM expense_items")
    fun getAllExpenseItems() : Flow<List<ExpenseItem>>

    @Query("DELETE FROM expense_items")
    suspend fun deleteAllItems()
}