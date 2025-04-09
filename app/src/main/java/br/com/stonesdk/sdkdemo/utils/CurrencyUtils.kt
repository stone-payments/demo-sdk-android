package br.com.stonesdk.sdkdemo.utils


fun Long.parseCentsToCurrency(): String {
    return "R$ ${this / 100},${(this % 100).toString().padEnd(2, '0')}"
}

fun String.parseCurrencyToCents(): Long {
    this.replace(Regex("[^0-9]"), "").let {
        return if (it.isEmpty()) 0 else it.toLong()
    }
}