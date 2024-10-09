package com.zen.accounts.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zen.accounts.data.db.model.BackupTracker

@Dao
interface BackupTrackerDao {
    @Insert
    suspend fun insertBackupTracker(notBackupExpense : BackupTracker)

    @Insert
    suspend fun insertBackupTracker(notBackupExpense: List<BackupTracker>)

    @Query("SELECT * FROM backup_tracker WHERE expenseId = :expenseId")
    suspend fun getBackupTracker(expenseId : Long) : BackupTracker?

    @Query("SELECT COUNT(*) FROM backup_tracker")
    suspend fun getTableEntryCount() : Long

    @Query("SELECT expenseId from backup_tracker where operation = 'delete'")
    fun getDeletedExpensesId() : List<Long>

    @Query("DELETE FROM backup_tracker WHERE expenseId = :expenseId")
    suspend fun deleteRecord(expenseId: Long)
    @Query("DELETE FROM backup_tracker WHERE operation = 'delete'")
    suspend fun deleteDeletedExpense()

    @Query("DELETE FROM backup_tracker WHERE operation = 'create'")
    suspend fun deleteCreatedExpense()

    @Query("DELETE FROM backup_tracker WHERE operation = 'update'")
    suspend fun deleteUpdatedExpense()

    @Query("DELETE FROM backup_tracker")
    suspend fun deleteAll()
}