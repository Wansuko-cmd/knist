package com.wsr.batch.operation.matmul

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

infix fun IOType.D2.matMul(other: Batch<IOType.D1>) = other.map { this matMul it }

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: IOType.D2): Batch<IOType.D2> = map { it matMul other }

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: Batch<IOType.D2>): Batch<IOType.D2> = mapWith(other) { a, b -> a matMul b }
