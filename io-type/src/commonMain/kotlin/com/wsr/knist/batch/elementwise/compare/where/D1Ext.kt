package com.wsr.knist.batch.elementwise.compare.where

import com.wsr.knist.Backend
import com.wsr.knist.batch.Batch
import com.wsr.knist.batch.d1
import com.wsr.knist.core.IOType
import com.wsr.knist.scope.ScopeOp
import com.wsr.knist.scope.ScopeOpDefault
import kotlin.jvm.JvmName

@JvmName("batchWhereFloatToFloatAsD1")
@ScopeOp
fun where(condition: Batch<IOType.D1>, onTrue: Float, onFalse: Float): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d1(condition.size, condition.shape, result)
}

@JvmName("batchWhereFloatToD1s")
@ScopeOp
fun where(condition: Batch<IOType.D1>, onTrue: Float, onFalse: Batch<IOType.D1>): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d1(condition.size, condition.shape, result)
}

@JvmName("batchWhereD1sToFloat")
@ScopeOp
fun where(condition: Batch<IOType.D1>, onTrue: Batch<IOType.D1>, onFalse: Float): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d1(condition.size, condition.shape, result)
}

@JvmName("batchWhereD1sToD1s")
@ScopeOp
fun where(condition: Batch<IOType.D1>, onTrue: Batch<IOType.D1>, onFalse: Batch<IOType.D1>): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d1(condition.size, condition.shape, result)
}

@JvmName("batchFloatWhereFloat")
@ScopeOp
fun Batch<IOType.D1>.where(condition: Batch<IOType.D1>, onTrue: Float, onFalse: Float): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse)
    return Batch.d1(size, shape, result)
}

@JvmName("batchFloatWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D1>.where(onTrue: Float, onFalse: Float, condition: (Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchFloatWhereD1s")
@ScopeOp
fun Batch<IOType.D1>.where(condition: Batch<IOType.D1>, onTrue: Float, @ScopeOpDefault("this")onFalse: Batch<IOType.D1> = this): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue, onFalse.value)
    return Batch.d1(size, shape, result)
}

@JvmName("batchFloatWhereD1sWithLambda")
@ScopeOp
inline fun Batch<IOType.D1>.where(onTrue: Float, @ScopeOpDefault("this") onFalse: Batch<IOType.D1> = this, condition: (Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D1.Global> =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD1sWhereFloat")
@ScopeOp
fun Batch<IOType.D1>.where(condition: Batch<IOType.D1>, @ScopeOpDefault("this") onTrue: Batch<IOType.D1> = this, onFalse: Float): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sWhereFloatWithLambda")
@ScopeOp
inline fun Batch<IOType.D1>.where(@ScopeOpDefault("this") onTrue: Batch<IOType.D1> = this, onFalse: Float, condition: (Batch<IOType.D1>) -> Batch<IOType.D1>) =
    where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))

@JvmName("batchD1sWhereD1s")
@ScopeOp
fun Batch<IOType.D1>.where(condition: Batch<IOType.D1>, @ScopeOpDefault("this") onTrue: Batch<IOType.D1> = this, @ScopeOpDefault("this") onFalse: Batch<IOType.D1> = this): Batch<IOType.D1.Global> {
    val result = Backend.where(condition.value, onTrue.value, onFalse.value)
    return Batch.d1(size, shape, result)
}

@JvmName("batchD1sWhereD1sWithLambda")
@ScopeOp
inline fun Batch<IOType.D1>.where(
    @ScopeOpDefault("this") onTrue: Batch<IOType.D1> = this,
    @ScopeOpDefault("this") onFalse: Batch<IOType.D1> = this,
    condition: (Batch<IOType.D1>) -> Batch<IOType.D1>,
) = where(onTrue = onTrue, onFalse = onFalse, condition = condition(this))
