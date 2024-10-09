package com.zen.accounts.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zen.accounts.presentation.utility.DateStringConverter
import java.util.Date

@Entity(tableName = "backup_tracker")
@TypeConverters(DateStringConverter::class)
data class BackupTracker(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    var expenseId : Long = 0L,
    var operation : String = "",
    var date : Date = Date()
)
