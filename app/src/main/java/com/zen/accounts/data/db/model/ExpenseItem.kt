package com.zen.accounts.data.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.zen.accounts.presentation.utility.DateStringConverter
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity("expense_items")
@TypeConverters(DateStringConverter::class)
@com.google.errorprone.annotations.Keep
@Parcelize
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) var id : Long,
    var itemTitle : String,
    var itemAmount : Double?,
    var lastUpdate : Date?
): Parcelable {
    constructor() : this(0L, "", 0.0, Date())
}
