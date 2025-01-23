package com.zen.accounts.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.presentation.utility.DateStringConverter
import com.zen.accounts.presentation.utility.ListOfExpenseItemToStringConverter
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExpense(expense : Expense)

    @Insert
    suspend fun insertExpenseList(expenses: List<Expense>)

    @Query("DELETE FROM expenses")
    suspend fun clearTable()

    @Query("DELETE FROM expenses WHERE id IN (:expenses)")
    suspend fun deleteExpenses(expenses : List<Long>)

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun getExpense(id : Long) : Expense?

    @Query(
        "SELECT e.id, " +
                "e.title,e.items, " +
                "e.totalAmount," +
                "e.date," +
                "b.operation as operation " +
                "FROM expenses e " +
                "LEFT JOIN " +
                "backup_tracker b " +
                "ON e.id = b.expenseId " +
                "ORDER BY e.id DESC"
    )
    fun getAllExpensesWithStatus() : Flow<List<ExpenseWithOperation>>

    @Query(
        "SELECT e.id, " +
                "e.title,e.items, " +
                "e.totalAmount," +
                "e.date," +
                "b.operation as operation " +
                "FROM expenses e " +
                "LEFT JOIN " +
                "backup_tracker b " +
                "ON e.id = b.expenseId " +
                "ORDER BY e.id DESC" // e.id is Long value of currentTime in millis at the time of creation of the record.
    )
    fun getExpensesWithStatusForTesting() : Flow<List<ExpenseWithOperation>>

    @Query("SELECT * FROM expenses")
    fun getAllExpenses() : Flow<List<Expense>>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Query("SELECT e.id, e.title, e.items, e.totalAmount, e.date  FROM expenses e INNER JOIN backup_tracker b on e.id = b.expenseId WHERE operation = 'create'")
    fun getCreatedExpenses() : List<Expense>

    @Query("SELECT e.id, e.title, e.items, e.totalAmount, e.date  FROM expenses e INNER JOIN backup_tracker b on e.id = b.expenseId WHERE operation = 'update'")
    fun getUpdatedExpenses() : List<Expense>
    
    @Query("SELECT * FROM expenses WHERE id > :fromDate AND id < :toDate")
    fun getExpenseInRange(fromDate : Long, toDate : Long) : Flow<List<Expense>>

}

@TypeConverters(ListOfExpenseItemToStringConverter::class, DateStringConverter::class)
data class ExpenseWithOperation(
    var id : Long = 0L,
    var title : String = "",
    val items : ArrayList<ExpenseItem> = arrayListOf(),
    var totalAmount : Double = 0.0,
    var date : Date = Date(),
    var operation : String? = null
) {
    fun toExpense() : Expense {
        return Expense(
            id, title, items, totalAmount, date
        )
    }
}
