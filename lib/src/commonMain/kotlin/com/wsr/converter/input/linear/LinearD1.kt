package com.wsr.converter.input.linear

import com.wsr.IOType
import com.wsr.converter.input.InputConverter
import kotlinx.serialization.Serializable

@Serializable
class LinearD1(override val outputSize: Int) : InputConverter.D1<IOType.D1>() {
    override fun convert(input: List<IOType.D1>): List<IOType.D1> = input
}
