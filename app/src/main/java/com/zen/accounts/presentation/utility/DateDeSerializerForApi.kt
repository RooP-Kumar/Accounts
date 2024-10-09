package com.zen.accounts.presentation.utility

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_with_time
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateDeSerializerForApi: JsonDeserializer<Date> {
    private val formatter = SimpleDateFormat(date_formatter_pattern_with_time, Locale.UK)
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        val stringDate = json?.asString ?: "10/Jan/2024"
        return formatter.parse(stringDate)!!
    }
}