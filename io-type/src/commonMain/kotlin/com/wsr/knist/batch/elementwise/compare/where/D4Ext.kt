package com.wsr.knist.batch.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d4
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchWhereFloatToFloatAsD4")
@ScopeOp
fun where(condition: Batch<IOType.D4>, onTrue: Float, onFalse: Float): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d4(condition.size, condition.shape, result)
}

@JvmName("batchWhereFloatToD4s")
@ScopeOp
fun where(condition: Batch<IOType.D4>, onTrue: Float, onFalse: Batch<IOType.D4>): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d4(condition.size, condition.shape, result)
}

@JvmName("batchWhereD4sToFloat")
@ScopeOp
fun where(condition: Batch<IOType.D4>, onTrue: Batch<IOType.D4>, onFalse: Float): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d4(condition.size, condition.shape, result)
}

@JvmName("batchWhereD4sToD4s")
@ScopeOp
fun where(condition: Batch<IOType.D4>, onTrue: Batch<IOType.D4>, onFalse: Batch<IOType.D4>): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d4(condition.size, condition.shape, result)
}

@JvmName("batchFloatWhereFloat")
@ScopeOp
fun Batch<IOType.D4>.where(condition: Batch<IOType.D4>, onTrue: Float, onFalse: Float): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d4(size, shape, result)
}

@JvmName("batchFloatWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D4>.where(onTrue: Float, onFalse: Float, condition: (Batch<IOType.D4>) -> Batch<IOType.D4>): Batch<IOType.D4.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchFloatWhereD4s")
@ScopeOp
fun Batch<IOType.D4>.where(condition: Batch<IOType.D4>, onTrue: Float, @ScopeOpDefault("this")onFalse: Batch<IOType.D4> = this): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d4(size, shape, result)
}

@JvmName("batchFloatWhereD4sWithLambda")
@ScopeOp
inline fun Batch<IOType.D4>.where(onTrue: Float, @ScopeOpDefault("this") onFalse: Batch<IOType.D4> = this, condition: (Batch<IOType.D4>) -> Batch<IOType.D4>): Batch<IOType.D4.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD4sWhereFloat")
@ScopeOp
fun Batch<IOType.D4>.where(condition: Batch<IOType.D4>, @ScopeOpDefault("this") onTrue: Batch<IOType.D4> = this, onFalse: Float): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d4(size, shape, result)
}

@JvmName("batchD4sWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D4>.where(@ScopeOpDefault("this") onTrue: Batch<IOType.D4> = this, onFalse: Float, condition: (Batch<IOType.D4>) -> Batch<IOType.D4>) =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD4sWhereD4s")
@ScopeOp
fun Batch<IOType.D4>.where(condition: Batch<IOType.D4>, @ScopeOpDefault("this") onTrue: Batch<IOType.D4> = this, @ScopeOpDefault("this") onFalse: Batch<IOType.D4> = this): Batch<IOType.D4.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d4(size, shape, result)
}

@JvmName("batchD4sWhereD4sWithLambda")
@ScopeOp
inline fun Batch<IOType.D4>.where(
    @ScopeOpDefault("this") onTrue: Batch<IOType.D4> = this,
    @ScopeOpDefault("this") onFalse: Batch<IOType.D4> = this,
    condition: (Batch<IOType.D4>) -> Batch<IOType.D4>,
) = where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))
