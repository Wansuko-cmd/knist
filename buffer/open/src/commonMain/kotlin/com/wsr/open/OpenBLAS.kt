package com.wsr.open

import com.wsr.base.IBLAS

val openBLAS: IBLAS = loadOpenBLAS() ?: object : IBLAS {}

expect fun loadOpenBLAS(): IBLAS?
