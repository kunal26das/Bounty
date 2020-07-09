/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.utils

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by kunal on 22-11-2019.
 */

object CalendarUtils {

    val LOCALE_INDIA = Locale("en", "in")
    private const val FORMAT_DATE_DISPLAY = "dd MMM yyyy"
    private const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"

    val simpleDateFormat = SimpleDateFormat(FORMAT_DATE_TIME, LOCALE_INDIA)
    val Long.date get() = simpleDateFormat.format(this).toString().date
    val currentDate get() = simpleDateFormat.format(Calendar.getInstance().time).toString()

    val String.date: String
        get() = run {
            if (isBlank()) return this
            val inputDateFormat = SimpleDateFormat(FORMAT_DATE_TIME, LOCALE_INDIA)
            val outputDateFormat = SimpleDateFormat(FORMAT_DATE_DISPLAY, LOCALE_INDIA)
            return outputDateFormat.format(inputDateFormat.parse(this)!!).toString()
        }
}