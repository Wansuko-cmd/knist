package com.wsr.knist.core.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.core.D2
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun where(condition: IOType.D2, onTrue: Float, onFalse: Float): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return IOType.D2(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D2, onTrue: Float, onFalse: IOType.D2): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D2(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D2, onTrue: IOType.D2, onFalse: Float): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D2(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D2, onTrue: IOType.D2, onFalse: IOType.D2): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D2(shape = condition.shape, value = result)
}

@ScopeOp
inline fun IOType.D2.where(onTrue: Float, onFalse: Float, condition: (IOType.D2) -> IOType.D2): IOType.D2.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D2.where(condition: IOType.D2, onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D2 = this): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D2(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D2.where(onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D2 = this, condition: (IOType.D2) -> IOType.D2): IOType.D2.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D2.where(condition: IOType.D2, @ScopeOpDefault("this") onTrue: IOType.D2 = this, onFalse: Float): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D2(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D2.where(@ScopeOpDefault("this") onTrue: IOType.D2 = this, onFalse: Float, condition: (IOType.D2) -> IOType.D2): IOType.D2.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D2.where(condition: IOType.D2, @ScopeOpDefault("this") onTrue: IOType.D2 = this, @ScopeOpDefault("this") onFalse: IOType.D2 = this): IOType.D2.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D2(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D2.where(@ScopeOpDefault("this") onTrue: IOType.D2 = this, @ScopeOpDefault("this") onFalse: IOType.D2 = this, condition: (IOType.D2) -> IOType.D2): IOType.D2.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)
