package com.wsr.knist.core.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.core.D0
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.Companion.random(from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D0.Global {
    val result = Backend.random(size = 1, from = from, until = until, random = random)
    return IOType.D0(value = result)
}
