package com.wsr.batch.sum

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.collection.sum
import com.wsr.get

fun Batch<IOType.D2>.sum(axis: Int) = Batch(size) { this[it].sum(axis) }
