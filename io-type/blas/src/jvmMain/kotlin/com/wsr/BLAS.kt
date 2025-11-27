package com.wsr

actual val default: IBLAS = openBLAS ?: object : IBLAS {}
