package com.wsr

actual object BLAS : IBLAS by loadJBLAS() ?: object : IBLAS {}
