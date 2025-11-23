package com.wsr.batch.operation.matmul

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.batch.collecction.map.mapWith
import com.wsr.core.IOType
import com.wsr.core.operation.matmul.matMul

infix fun IOType.D3.matMul(other: Batch<IOType.D3>) = other.map { this matMul it }

@JvmName("matMulToD3s")
infix fun Batch<IOType.D3>.matMul(other: IOType.D3): Batch<IOType.D3> = map { it matMul other }

@JvmName("matMulToD3s")
infix fun Batch<IOType.D3>.matMul(other: Batch<IOType.D3>): Batch<IOType.D3> = mapWith(other) { a, b -> a matMul b }
