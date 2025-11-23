package com.wsr.batch.math

import com.wsr.batch.Batch
import com.wsr.batch.collecction.map.map
import com.wsr.core.IOType
import com.wsr.core.math.softmax

@JvmName("batchD1sSoftmax")
fun Batch<IOType.D1>.softmax(): Batch<IOType.D1> = map { it.softmax() }

@JvmName("batchD2sSoftmax")
fun Batch<IOType.D2>.softmax(): Batch<IOType.D2> = map { it.softmax() }

@JvmName("batchD2sSoftmaxWithAxis")
fun Batch<IOType.D2>.softmax(axis: Int): Batch<IOType.D2> = map { it.softmax(axis = axis) }

@JvmName("batchD3sSoftmax")
fun Batch<IOType.D3>.softmax(): Batch<IOType.D3> = map { it.softmax() }

@JvmName("batchD3sSoftmaxWithAxis")
fun Batch<IOType.D3>.softmax(axis: Int): Batch<IOType.D3> = map { it.softmax(axis = axis) }
