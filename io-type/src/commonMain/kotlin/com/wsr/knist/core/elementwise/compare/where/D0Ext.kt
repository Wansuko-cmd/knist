package com.wsr.knist.core.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.core.D0
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun where(condition: IOType.D0, onTrue: Float, onFalse: Float): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return IOType.D0(result)
}

@ScopeOp
fun where(condition: IOType.D0, onTrue: Float, onFalse: IOType.D0): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D0(result)
}

@ScopeOp
fun where(condition: IOType.D0, onTrue: IOType.D0, onFalse: Float): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D0(result)
}

@ScopeOp
fun where(condition: IOType.D0, onTrue: IOType.D0, onFalse: IOType.D0): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D0(result)
}

@ScopeOp
inline fun IOType.D0.where(onTrue: Float, onFalse: Float, condition: (IOType.D0) -> IOType.D0): IOType.D0.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D0.where(condition: IOType.D0, onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D0 = this): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D0(result)
}

@ScopeOp
inline fun IOType.D0.where(onTrue: Float, onFalse: IOType.D0, condition: (IOType.D0) -> IOType.D0): IOType.D0.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D0.where(condition: IOType.D0, @ScopeOpDefault("this") onTrue: IOType.D0 = this, onFalse: Float): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D0(result)
}

@ScopeOp
inline fun IOType.D0.where(@ScopeOpDefault("this") onTrue: IOType.D0 = this, onFalse: Float, condition: (IOType.D0) -> IOType.D0): IOType.D0.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D0.where(condition: IOType.D0, @ScopeOpDefault("this") onTrue: IOType.D0 = this, @ScopeOpDefault("this") onFalse: IOType.D0 = this): IOType.D0.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D0(result)
}

@ScopeOp
inline fun IOType.D0.where(@ScopeOpDefault("this") onTrue: IOType.D0 = this, onFalse: IOType.D0, condition: (IOType.D0) -> IOType.D0): IOType.D0.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)
