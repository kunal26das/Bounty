/*
 * Copyright (c) 2020.
 */

package kudos26.bounty.utils

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by kunal on 22-11-2019.
 */

private val LOCALE_INDIA = Locale("en", "in")
private const val FORMAT_DATE_STORE = "yyyy-MM-dd"
private const val FORMAT_DATE_DISPLAY = "dd MMM yyyy"
private const val FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss"
private val simpleDateFormat = SimpleDateFormat(FORMAT_DATE_TIME, LOCALE_INDIA)

fun getCurrentDate(): String {
    return simpleDateFormat.format(Calendar.getInstance().time).toString().split(' ')[0]
}

fun String.toDisplayDate(): String {
    if (isBlank()) return this
    val inputDateFormat = SimpleDateFormat(FORMAT_DATE_STORE, LOCALE_INDIA)
    val outputDateFormat = SimpleDateFormat(FORMAT_DATE_DISPLAY, LOCALE_INDIA)
    return outputDateFormat.format(inputDateFormat.parse(this)!!).toString()
}