package com.zen.accounts.presentation.utility

import androidx.room.TypeConverter
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_with_time
import java.text.SimpleDateFormat
import java.util.Date

class DateStringConverter {
    @TypeConverter
    fun dateToString(date: Date) : String {
        val formatter = SimpleDateFormat(date_formatter_pattern_with_time, java.util.Locale.UK)
        return formatter.format(date)
    }

    @TypeConverter
    fun stringToDate(dateString : String) : Date {
        val formatter = SimpleDateFormat(date_formatter_pattern_with_time, java.util.Locale.UK)
        return formatter.parse(dateString)!!
    }
}

//LLL dd, yyyy hh:mm:ss a
