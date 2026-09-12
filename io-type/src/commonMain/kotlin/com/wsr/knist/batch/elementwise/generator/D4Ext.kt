package com.wsr.knist.batch.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun Batch.Companion.random(size: Int, i: Int, j: Int, k: Int, l: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D4.Global> {
    val result = Backend.random(size = size * i * j * k * l, from = from, until = until, random = random)
    return Batch.d4(size = size, i = i, j = j, k = k, l = l, value = result)
}
