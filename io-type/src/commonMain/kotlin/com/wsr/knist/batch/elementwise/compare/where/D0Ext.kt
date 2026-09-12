package com.wsr.knist.batch.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d0
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchWhereFloatToFloatAsD0")
@ScopeOp
fun where(condition: Batch<IOType.D0>, onTrue: Float, onFalse: Float): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d0(condition.size, result)
}

@JvmName("batchWhereFloatToD0s")
@ScopeOp
fun where(condition: Batch<IOType.D0>, onTrue: Float, onFalse: Batch<IOType.D0>): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d0(condition.size, result)
}

@JvmName("batchWhereD0sToFloat")
@ScopeOp
fun where(condition: Batch<IOType.D0>, onTrue: Batch<IOType.D0>, onFalse: Float): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d0(condition.size, result)
}

@JvmName("batchWhereD0sToD0s")
@ScopeOp
fun where(condition: Batch<IOType.D0>, onTrue: Batch<IOType.D0>, onFalse: Batch<IOType.D0>): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d0(condition.size, result)
}

@JvmName("batchFloatWhereFloat")
@ScopeOp
fun Batch<IOType.D0>.where(condition: Batch<IOType.D0>, onTrue: Float, onFalse: Float): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d0(size, result)
}

@JvmName("batchFloatWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D0>.where(onTrue: Float, onFalse: Float, condition: (Batch<IOType.D0>) -> Batch<IOType.D0>): Batch<IOType.D0.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchFloatWhereD0s")
@ScopeOp
fun Batch<IOType.D0>.where(condition: Batch<IOType.D0>, onTrue: Float, @ScopeOpDefault("this") onFalse: Batch<IOType.D0> = this): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d0(size, result)
}

@JvmName("batchFloatWhereD0sWithLambda")
@ScopeOp
inline fun Batch<IOType.D0>.where(onTrue: Float, @ScopeOpDefault("this") onFalse: Batch<IOType.D0> = this, condition: (Batch<IOType.D0>) -> Batch<IOType.D0>): Batch<IOType.D0.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD0sWhereFloat")
@ScopeOp
fun Batch<IOType.D0>.where(condition: Batch<IOType.D0>, @ScopeOpDefault("this") onTrue: Batch<IOType.D0> = this, onFalse: Float): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d0(size, result)
}

@JvmName("batchD0sWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D0>.where(@ScopeOpDefault("this") onTrue: Batch<IOType.D0> = this, onFalse: Float, condition: (Batch<IOType.D0>) -> Batch<IOType.D0>) =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD0sWhereD0s")
@ScopeOp
fun Batch<IOType.D0>.where(condition: Batch<IOType.D0>, @ScopeOpDefault("this") onTrue: Batch<IOType.D0> = this, @ScopeOpDefault("this") onFalse: Batch<IOType.D0> = this): Batch<IOType.D0.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d0(size, result)
}

@JvmName("batchD0sWhereD0sWithLambda")
@ScopeOp
inline fun Batch<IOType.D0>.where(
    @ScopeOpDefault("this") onTrue: Batch<IOType.D0> = this,
    @ScopeOpDefault("this") onFalse: Batch<IOType.D0> = this,
    condition: (Batch<IOType.D0>) -> Batch<IOType.D0>,
) = where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))
