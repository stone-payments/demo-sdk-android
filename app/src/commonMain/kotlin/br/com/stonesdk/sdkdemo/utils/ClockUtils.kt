package br.com.stonesdk.sdkdemo.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentFormattedTimestamp(): String {
    val now = Clock.System.now()
    val timeZone = TimeZone.currentSystemDefault()
    val localDateTime = now.toLocalDateTime(timeZone)
    return localDateTime.toString()
}