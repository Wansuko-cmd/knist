package com.wsr.cl

import com.wsr.base.IBLAS

val cLBlast: IBLAS = loadCLBlast() ?: object : IBLAS {}

expect fun loadCLBlast(): IBLAS?
