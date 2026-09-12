package com.wsr.knist.gpu

import com.wsr.knist.base.IBackend

expect fun loadGPUBackend(fallback: IBackend, maxReservedBytes: Long = 4_000_000_000L, maxPoolBytes: Long = maxReservedBytes, enableProfiler: Boolean = false): IBackend
