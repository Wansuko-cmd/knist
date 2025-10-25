package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.converter.Converter
import kotlinx.serialization.Serializable

@Serializable
class LinearD1(override val outputSize: Int) : Converter.D1<IOType.D1>() {
    override fun encode(input: List<IOType.D1>): List<IOType.D1> = input
    override fun decode(input: List<IOType.D1>): List<IOType.D1> = input
}
