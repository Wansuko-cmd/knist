package com.wsr.converter.output.linear

import com.wsr.IOType
import com.wsr.converter.output.OutputConverter
import kotlinx.serialization.Serializable

@Serializable
class OutputLinearD3(
    override val inputX: Int,
    override val inputY: Int,
    override val inputZ: Int,
) : OutputConverter.D3<IOType.D3>() {
    override fun convert(output: List<IOType.D3>): List<IOType.D3> = output
}
