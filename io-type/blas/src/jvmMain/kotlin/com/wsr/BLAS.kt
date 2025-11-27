package com.wsr

actual object Default : IBLAS by loadJBLAS() ?: object : IBLAS {}
