package com.wsr.knist.gpu

import com.wsr.knist.base.IBackend
import com.wsr.knist.base.loadNativeLibrary

private const val LIB_PATH = "gpu"
private const val LIB_NAME = "gpu"

actual fun loadGPUBackend(fallback: IBackend, maxReservedBytes: Long, maxPoolBytes: Long, enableProfiler: Boolean): IBackend {
    val isSuccess = loadNativeLibrary(path = LIB_PATH, name = LIB_NAME)
    if (isSuccess) gpuMaxReservedBytes = maxReservedBytes
    return if (isSuccess) GPUBackend(fallback, maxPoolBytes, enableProfiler) else fallback
}
