package com.wsr.knist.core.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.Companion.random(i: Int, j: Int, k: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D3.Global {
    val result = Backend.random(size = i * j * k, from = from, until = until, random = random)
    return IOType.D3(shape = listOf(i, j, k), value = result)
}
