package com.wsr.knist.core.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.core.D3
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun where(condition: IOType.D3, onTrue: Float, onFalse: Float): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return IOType.D3(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D3, onTrue: Float, onFalse: IOType.D3): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D3(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D3, onTrue: IOType.D3, onFalse: Float): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D3(shape = condition.shape, value = result)
}

@ScopeOp
fun where(condition: IOType.D3, onTrue: IOType.D3, onFalse: IOType.D3): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D3(shape = condition.shape, value = result)
}

@ScopeOp
inline fun IOType.D3.where(onTrue: Float, onFalse: Float, condition: (IOType.D3) -> IOType.D3): IOType.D3.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D3.where(condition: IOType.D3, onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D3 = this): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D3(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D3.where(onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D3 = this, condition: (IOType.D3) -> IOType.D3): IOType.D3.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D3.where(condition: IOType.D3, @ScopeOpDefault("this") onTrue: IOType.D3 = this, onFalse: Float): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D3(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D3.where(@ScopeOpDefault("this") onTrue: IOType.D3 = this, onFalse: Float, condition: (IOType.D3) -> IOType.D3): IOType.D3.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D3.where(condition: IOType.D3, @ScopeOpDefault("this") onTrue: IOType.D3 = this, @ScopeOpDefault("this") onFalse: IOType.D3 = this): IOType.D3.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D3(shape = shape, value = result)
}

@ScopeOp
inline fun IOType.D3.where(@ScopeOpDefault("this") onTrue: IOType.D3 = this, @ScopeOpDefault("this") onFalse: IOType.D3 = this, condition: (IOType.D3) -> IOType.D3): IOType.D3.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)
