package com.zen.accounts.data.db.model

import android.net.Uri
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.zen.accounts.presentation.utility.DateStringConverter
import com.zen.accounts.presentation.utility.ListOfExpenseItemToStringConverter
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity("expenses")
@TypeConverters(ListOfExpenseItemToStringConverter::class, DateStringConverter::class)
@com.google.errorprone.annotations.Keep
@Parcelize
data class Expense(
    @PrimaryKey
    var id : Long,
    var title : String,
    val items : ArrayList<ExpenseItem>,
    var totalAmount : Double,
    var date : Date?
) : Parcelable {
    constructor() : this(0L, "", arrayListOf(), 0.0, Date())
    override fun toString() : String {
        return Uri.encode(Gson().toJson(this))
    }
}
