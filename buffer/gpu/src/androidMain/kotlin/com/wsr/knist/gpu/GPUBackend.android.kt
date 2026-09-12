package com.wsr.knist.gpu

import com.wsr.knist.base.IBackend

actual fun loadGPUBackend(fallback: IBackend, maxReservedBytes: Long, maxPoolBytes: Long, enableProfiler: Boolean): IBackend = runCatching<IBackend> {
    System.loadLibrary("gpu")
    gpuMaxReservedBytes = maxReservedBytes
    GPUBackend(fallback, maxPoolBytes, enableProfiler)
}.getOrElse { fallback }
