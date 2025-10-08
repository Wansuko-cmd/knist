package com.wsr

expect object BLAS : IBLAS

interface IBLAS {
    val isNative: Boolean get() = false
}
