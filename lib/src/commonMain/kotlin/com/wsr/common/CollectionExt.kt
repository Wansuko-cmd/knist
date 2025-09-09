package com.wsr.common

internal inline fun <T> List<T>.averageOf(selector: (T) -> Double) = map(selector).average()
