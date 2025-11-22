package com.wsr.batch.func

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.batch.collection.map
import com.wsr.func.softmax

@JvmName("batchD1sSoftmax")
fun Batch<IOType.D1>.softmax(): Batch<IOType.D1> = map { it.softmax() }

@JvmName("batchD2sSoftmax")
fun Batch<IOType.D2>.softmax(): Batch<IOType.D2> = map { it.softmax() }

@JvmName("batchD2sSoftmaxWithAxis")
fun Batch<IOType.D2>.softmax(axis: Int): Batch<IOType.D2> = map { it.softmax(axis = axis) }

@JvmName("batchD3sSoftmax")
fun Batch<IOType.D3>.softmax(): Batch<IOType.D3> = map { it.softmax() }
