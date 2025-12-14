package com.wsr.cl

import com.wsr.blas.base.IBLAS

val cLBlast: IBLAS = loadCLBlast() ?: object : IBLAS {}

expect fun loadCLBlast(): IBLAS?
