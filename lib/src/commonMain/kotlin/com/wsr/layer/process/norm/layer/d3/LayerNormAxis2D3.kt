package com.wsr.layer.process.norm.layer.d3

import com.wsr.Batch
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
class LayerNormAxis2D3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Process.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        TODO("axis2の次元で正規化する")
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        TODO("axis2の次元で正規化する")
    }
}
