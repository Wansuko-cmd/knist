package com.wsr.knist.network.converter.raw

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class RawD2(override val i: Int, override val j: Int) : Converter.D2<Batch<IOType.D2>>() {
    override fun encode(input: Batch<IOType.D2>): Batch<IOType.D2> = input
    override fun decode(input: Batch<IOType.D2>): Batch<IOType.D2> = input
}
