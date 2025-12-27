package com.wsr.cpu

import com.wsr.base.IBackend
import com.wsr.base.KotlinBackend

val cpu: IBackend = loadCPUBackend() ?: KotlinBackend

expect fun loadCPUBackend(): IBackend?
