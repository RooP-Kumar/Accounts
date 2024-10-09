package com.zen.accounts.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zen.accounts.data.db.dao.BackupTrackerDao
import com.zen.accounts.data.db.dao.ExpenseDao
import com.zen.accounts.data.db.dao.ExpenseItemDao
import com.zen.accounts.data.db.model.BackupTracker
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.data.db.model.ExpenseItem

@Database([Expense::class, ExpenseItem::class, BackupTracker::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getExpenseDao() : ExpenseDao
    abstract fun getExpenseItemDao() : ExpenseItemDao
    abstract fun getBackupTrackerDao() : BackupTrackerDao
}