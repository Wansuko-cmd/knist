package com.wsr.converter.input.linear

import com.wsr.IOType
import com.wsr.converter.input.InputConverter
import kotlinx.serialization.Serializable

@Serializable
class LinearD3(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
) : InputConverter.D3<IOType.D3>() {
    override fun convert(input: List<IOType.D3>): List<IOType.D3> = input
}
