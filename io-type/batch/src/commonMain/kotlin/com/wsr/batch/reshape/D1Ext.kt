package com.wsr.batch.reshape

import com.wsr.Batch
import com.wsr.IOType
import com.wsr.get
import com.wsr.reshape.broadcastToD2

fun Batch<IOType.D1>.broadcastToD2(axis: Int, size: Int) = Batch(this.size) { this[it].broadcastToD2(axis, size) }
