package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.converter.InputConverter
import kotlinx.serialization.Serializable

@Serializable
class LinearD2(
    override val outputX: Int,
    override val outputY: Int,
) : InputConverter.D2<IOType.D2>() {
    override fun convert(input: List<IOType.D2>): List<IOType.D2> = input
}
