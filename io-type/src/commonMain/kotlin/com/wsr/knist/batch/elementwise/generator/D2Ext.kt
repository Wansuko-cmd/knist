package com.wsr.knist.batch.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d2
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun Batch.Companion.random(size: Int, i: Int, j: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D2.Global> {
    val result = Backend.random(size = size * i * j, from = from, until = until, random = random)
    return Batch.d2(size = size, i = i, j = j, value = result)
}
