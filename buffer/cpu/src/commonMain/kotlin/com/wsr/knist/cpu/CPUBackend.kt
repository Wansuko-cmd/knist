package com.wsr.knist.cpu

import com.wsr.knist.base.IBackend

expect fun loadCPUBackend(fallback: IBackend, maxReservedBytes: Long = 4_000_000_000, maxPoolBytes: Long = maxReservedBytes): IBackend
