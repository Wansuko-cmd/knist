package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class LinearD2(override val outputX: Int, override val outputY: Int) : Converter.D2<IOType.D2>() {
    override fun encode(input: List<IOType.D2>): List<IOType.D2> = input
    override fun decode(input: List<IOType.D2>): List<IOType.D2> = input
}
