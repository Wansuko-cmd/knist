package com.wsr.open

import com.wsr.blas.base.IBLAS

val openBLAS: IBLAS = loadOpenBLAS() ?: object : IBLAS {}

expect fun loadOpenBLAS(): IBLAS?
