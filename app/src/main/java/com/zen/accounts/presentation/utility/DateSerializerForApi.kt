package com.zen.accounts.presentation.utility

import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_with_time
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateSerializerForApi: JsonSerializer<Date> {
    private val formatter = SimpleDateFormat(date_formatter_pattern_with_time, Locale.UK)
    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return context?.serialize(formatter.format(src!!))!!
    }
}