package com.wsr.core.operation.inner

import com.wsr.Backend
import com.wsr.core.IOType

infix fun IOType.D1.inner(other: IOType.D1): Float = Backend.inner(x = value, y = other.value, b = 1)[0]
