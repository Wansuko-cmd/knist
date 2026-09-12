package com.wsr.knist.core.reduction

import com.wsr.knist.Backend
import com.wsr.knist.core.D0
import com.wsr.knist.core.D1
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.random.Random

@ScopeOp
fun IOType.D2.average(): IOType.D0.Global = IOType.D0(Backend.average(value))

@ScopeOp
fun IOType.D2.average(axis: Int): IOType.D1.Global {
    val result = Backend.average(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(value = result)
}

@ScopeOp
fun IOType.D2.max(): IOType.D0.Global = IOType.D0(Backend.max(x = value))

@ScopeOp
fun IOType.D2.max(axis: Int): IOType.D1.Global {
    val result = Backend.max(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

@ScopeOp
fun IOType.D2.min(): IOType.D0.Global = IOType.D0(Backend.min(x = value))

@ScopeOp
fun IOType.D2.min(axis: Int): IOType.D1.Global {
    val result = Backend.min(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

@ScopeOp
fun IOType.D2.sum(): IOType.D0.Global = IOType.D0(Backend.sum(x = value))

@ScopeOp
fun IOType.D2.sum(axis: Int): IOType.D1.Global {
    val result = Backend.sum(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

@ScopeOp
fun IOType.D2.maxIndex(axis: Int): IOType.D1.Global {
    val result = Backend.maxIndex(x = value, xi = i, xj = j, axis = axis)
    return IOType.D1(result)
}

@ScopeOp
fun IOType.D2.topK(k: Int, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D1.Global {
    val result = Backend.topK(x = value, xi = i, xj = j, k = k, axis = axis, random = random)
    return IOType.D1(result)
}

@ScopeOp
fun IOType.D2.topP(p: Float, axis: Int, @ScopeOpDefault("kotlin.random.Random") random: Random = Random): IOType.D1.Global {
    val result = Backend.topP(x = value, xi = i, xj = j, p = p, axis = axis, random = random)
    return IOType.D1(result)
}
