package com.wsr.batch.collecction.sum

import com.wsr.batch.Batch
import com.wsr.batch.get
import com.wsr.core.IOType
import com.wsr.core.collection.sum.sum
import com.wsr.core.d0

fun Batch<IOType.D2>.sum() = Batch(size) { IOType.d0(this[it].sum()) }

fun Batch<IOType.D2>.sum(axis: Int) = Batch(size) { this[it].sum(axis) }
