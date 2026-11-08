package com.wsr.knist.network.converter.raw

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOType
import com.wsr.knist.network.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class RawD3(override val i: Int, override val j: Int, override val k: Int) : Converter.D3<Batch<IOType.D3>>() {
    override fun encode(input: Batch<IOType.D3>): Batch<IOType.D3> = input
    override fun decode(input: Batch<IOType.D3>): Batch<IOType.D3> = input
}
