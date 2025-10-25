package com.wsr.converter.output.linear

import com.wsr.IOType
import com.wsr.converter.output.OutputConverter
import kotlinx.serialization.Serializable

@Serializable
class OutputLinearD1(override val inputSize: Int) : OutputConverter.D1<IOType.D1>() {
    override fun convert(output: List<IOType.D1>): List<IOType.D1> = output
}
