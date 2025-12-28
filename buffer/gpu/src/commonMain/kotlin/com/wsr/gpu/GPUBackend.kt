package com.wsr.gpu

import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend

val gpu: IBackend = loadGPUBackend() ?: KotlinBackend

expect fun loadGPUBackend(): IBackend?
