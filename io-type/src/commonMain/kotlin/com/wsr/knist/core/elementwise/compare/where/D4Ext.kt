package com.wsr.knist.core.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.core.D4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun where(condition: IOType.D4, onTrue: Float, onFalse: Float): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return IOType.D4(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D4, onTrue: Float, onFalse: IOType.D4): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D4(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D4, onTrue: IOType.D4, onFalse: Float): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D4(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D4, onTrue: IOType.D4, onFalse: IOType.D4): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D4(shape = condition.shape, value = result)
}

@ScopeOp
inline fun IOType.D4.where(onTrue: Float, onFalse: Float, condition: (IOType.D4) -> IOType.D4): IOType.D4.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D4.where(condition: IOType.D4, onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D4 = this): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D4(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D4.where(onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D4 = this, condition: (IOType.D4) -> IOType.D4): IOType.D4.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D4.where(condition: IOType.D4, @ScopeOpDefault("this") onTrue: IOType.D4 = this, onFalse: Float): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D4(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D4.where(@ScopeOpDefault("this") onTrue: IOType.D4 = this, onFalse: Float, condition: (IOType.D4) -> IOType.D4): IOType.D4.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D4.where(condition: IOType.D4, @ScopeOpDefault("this") onTrue: IOType.D4 = this, @ScopeOpDefault("this") onFalse: IOType.D4 = this): IOType.D4.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D4(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D4.where(@ScopeOpDefault("this") onTrue: IOType.D4 = this, @ScopeOpDefault("this") onFalse: IOType.D4 = this, condition: (IOType.D4) -> IOType.D4): IOType.D4.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)
