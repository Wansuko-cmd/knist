package com.wsr.knist.cpu

import com.wsr.knist.base.IBackend

actual fun loadCPUBackend(fallback: IBackend, maxReservedBytes: Long, maxPoolBytes: Long): IBackend = runCatching<IBackend> {
    System.loadLibrary("cpu")
    cpuMaxReservedBytes = maxReservedBytes
    CPUJvmBackend(fallback, maxPoolBytes)
}.getOrElse { fallback }
