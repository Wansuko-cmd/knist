package com.wsr.converter.output.linear

import com.wsr.IOType
import com.wsr.converter.output.OutputConverter
import kotlinx.serialization.Serializable

@Serializable
class OutputLinearD2(
    override val inputX: Int,
    override val inputY: Int,
) : OutputConverter.D2<IOType.D2>() {
    override fun convert(output: List<IOType.D2>): List<IOType.D2> = output
}
