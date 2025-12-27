package com.wsr.cpu

import com.wsr.base.KotlinBackend
import com.wsr.base.IBackend

val cpu: IBackend = loadCPUBackend() ?: KotlinBackend

expect fun loadCPUBackend(): IBackend?
