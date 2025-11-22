package com.wsr.batch.matmul

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.batch.collection.mapWith
import com.wsr.dot.matmul.matMul

infix fun IOType.D2.matMul(other: Batch<IOType.D1>) = other.map { this matMul it }

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: IOType.D2): Batch<IOType.D2> = map { it matMul other }

@JvmName("matMulToD2s")
infix fun Batch<IOType.D2>.matMul(other: Batch<IOType.D2>): Batch<IOType.D2> = mapWith(other) { a, b -> a matMul b }
