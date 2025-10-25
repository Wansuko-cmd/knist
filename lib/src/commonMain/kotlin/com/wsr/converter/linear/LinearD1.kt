package com.wsr.converter.linear

import com.wsr.IOType
import com.wsr.converter.InputConverter
import kotlinx.serialization.Serializable

@Serializable
class LinearD1(override val outputSize: Int) : InputConverter.D1<IOType.D1> {
    override fun convert(input: List<IOType.D1>): List<IOType.D1> = input
}
