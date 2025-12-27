package com.wsr

import com.wsr.base.IBackend
import com.wsr.cpu.cpu

object Backend : IBackend by cpu
