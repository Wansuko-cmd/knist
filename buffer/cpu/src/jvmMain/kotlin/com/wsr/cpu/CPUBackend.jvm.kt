package com.wsr.cpu

import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "cpu"
private const val LIB_NAME = "cpu"

actual fun loadCPUBackend(): IBackend? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) CPUBackend() else null
}

class CPUBackend : IBackend by KotlinBackend
