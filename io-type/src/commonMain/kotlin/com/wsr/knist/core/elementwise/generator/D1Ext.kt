package com.wsr.knist.core.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.core.D1
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.Companion.random(i: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D1.Global {
    val result = Backend.random(size = i, from = from, until = until, random = random)
    return IOType.D1(value = result)
}
