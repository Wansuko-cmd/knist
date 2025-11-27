package com.wsr.open

actual val default: IBLAS = openBLAS ?: object : IBLAS {}
