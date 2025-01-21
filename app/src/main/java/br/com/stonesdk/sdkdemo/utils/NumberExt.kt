package br.com.stonesdk.sdkdemo.utils

fun String.parseCurrencyToCents(): Long {
    this.replace(Regex("[^0-9]"), "").let {
        return if (it.isEmpty()) 0 else it.toLong()
    }
}