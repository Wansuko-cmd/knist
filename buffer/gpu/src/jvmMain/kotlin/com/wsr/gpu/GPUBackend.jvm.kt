package com.wsr.gpu

import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend
import com.wsr.base.loadNativeLibrary

private const val LIB_PATH = "gpu"
private const val LIB_NAME = "gpu"

actual fun loadGPUBackend(): IBackend? {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    return if (isSuccess) GPUBackend() else null
}

class GPUBackend : IBackend by KotlinBackend
