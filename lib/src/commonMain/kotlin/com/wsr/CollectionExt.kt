package com.wsr

inline fun <T> List<T>.averageOf(selector: (T) -> Double) = map(selector).average()
