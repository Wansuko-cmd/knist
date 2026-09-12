package com.wsr.knist.batch.elementwise.generator

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun Batch.Companion.random(size: Int, i: Int, from: Float, until: Float, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): Batch<IOType.D1.Global> {
    val result = Backend.random(size = size * i, from = from, until = until, random = random)
    return Batch.d1(size = size, i = i, value = result)
}
