package com.zen.accounts

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.zen.accounts.data.db.AppDatabase
import com.zen.accounts.data.db.dao.BackupTrackerDao
import com.zen.accounts.data.db.dao.ExpenseDao
import com.zen.accounts.data.db.model.BackupTracker
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.ExpenseItem
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_with_time
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_without_time
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var database : AppDatabase
    private lateinit var expenseDao : ExpenseDao
    private lateinit var backupDao: BackupTrackerDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        expenseDao = database.getExpenseDao()
        backupDao = database.getBackupTrackerDao()
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun ExpenseDaoAllExpenseWithOperation_returnExpenseWithOperation() = runTest {
        val currentTimeMills = System.currentTimeMillis()
        val expense = Expense(
            0L,
            "First Expense",
            arrayListOf(
                ExpenseItem(
                    0L,
                    "Demo Item Title",
                    0.0,
                    Date(currentTimeMills)
                )
            ),
            0.0,
            Date(System.currentTimeMillis())
        )
        val backupData = BackupTracker(
            1,
            expenseId = 0L,
            operation = "insert",
            Date(currentTimeMills)
        )
        expenseDao.insertExpense(expense)
        backupDao.insertBackupTracker(backupData)
        
        val formatter = SimpleDateFormat(date_formatter_pattern_with_time, Locale.UK)
        val formatter2 = SimpleDateFormat(date_formatter_pattern_without_time, Locale.UK)
        val result = expenseDao.getExpensesWithStatusForTesting().test {
            val result = awaitItem()
            assertThat(result.size).isGreaterThan(0)
            assertThat(result[0].items.size).isGreaterThan(0)
            assertThat(formatter.format(result[0].date)).isNotEqualTo(formatter2.format(currentTimeMills))
        }
    }
    
}
