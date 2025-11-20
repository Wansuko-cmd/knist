package com.wsr.layer.process.norm.layer.d3

import com.wsr.IOType
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import com.wsr.operator.div
import com.wsr.operator.minus
import com.wsr.operator.plus
import com.wsr.operator.times
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxis1D3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: List<IOType.D3>, context: Context): List<IOType.D3> {
        TODO("axis1の次元で正規化する")
    }

    override fun train(
        input: List<IOType.D3>,
        context: Context,
        calcDelta: (List<IOType.D3>) -> List<IOType.D3>,
    ): List<IOType.D3> {
        TODO("axis1の次元で正規化する")
    }
}
