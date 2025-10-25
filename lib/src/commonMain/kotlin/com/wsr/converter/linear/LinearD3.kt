package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class LinearD3(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : Converter.D3<IOType.D3>() {
    override fun encode(input: List<IOType.D3>): List<IOType.D3> = input
}
