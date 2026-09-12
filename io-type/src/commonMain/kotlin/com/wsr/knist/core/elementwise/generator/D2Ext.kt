package com.wsr.knist.core.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.core.D2
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.Companion.random(i: Int, j: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D2.Global {
    val result = Backend.random(size = i * j, from = from, until = until, random = random)
    return IOType.D2(shape = listOf(i, j), value = result)
}
