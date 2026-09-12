package com.wsr.knist.core.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.core.D1
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault

@ScopeOp
fun where(condition: IOType.D1, onTrue: Float, onFalse: Float): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return IOType.D1(result)
}

@ScopeOp
fun where(condition: IOType.D1, onTrue: Float, onFalse: IOType.D1): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D1(result)
}

@ScopeOp
fun where(condition: IOType.D1, onTrue: IOType.D1, onFalse: Float): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D1(result)
}

@ScopeOp
fun where(condition: IOType.D1, onTrue: IOType.D1, onFalse: IOType.D1): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D1(result)
}

@ScopeOp
inline fun IOType.D1.where(onTrue: Float, onFalse: Float, condition: (IOType.D1) -> IOType.D1): IOType.D1.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D1.where(condition: IOType.D1, onTrue: Float, @ScopeOpDefault("this") onFalse: IOType.D1 = this): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return IOType.D1(result)
}

@ScopeOp
inline fun IOType.D1.where(onTrue: Float, onFalse: IOType.D1, condition: (IOType.D1) -> IOType.D1): IOType.D1.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D1.where(condition: IOType.D1, @ScopeOpDefault("this") onTrue: IOType.D1 = this, onFalse: Float): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return IOType.D1(result)
}

@ScopeOp
inline fun IOType.D1.where(@ScopeOpDefault("this") onTrue: IOType.D1 = this, onFalse: Float, condition: (IOType.D1) -> IOType.D1): IOType.D1.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)

@ScopeOp
fun IOType.D1.where(condition: IOType.D1, @ScopeOpDefault("this") onTrue: IOType.D1 = this, @ScopeOpDefault("this") onFalse: IOType.D1 = this): IOType.D1.Global {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return IOType.D1(result)
}

@ScopeOp
inline fun IOType.D1.where(@ScopeOpDefault("this") onTrue: IOType.D1 = this, onFalse: IOType.D1, condition: (IOType.D1) -> IOType.D1): IOType.D1.Global = where(
    condition = condition(this),
    onTrue = onTrue,
    onFalse = onFalse,
)
