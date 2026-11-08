package com.wsr.knist.network.converter.raw

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class RawD1(override val i: Int) : Converter.D1<Batch<IOType.D1>>() {
    override fun encode(input: Batch<IOType.D1>): Batch<IOType.D1> = input
    override fun decode(input: Batch<IOType.D1>): Batch<IOType.D1> = input
}
