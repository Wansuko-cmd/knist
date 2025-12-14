package com.wsr.process.compute.norm.layer.d3

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.process.Context
import com.wsr.process.compute.Compute
import com.wsr.optimizer.Optimizer
import kotlinx.serialization.Serializable

@Serializable
class LayerNormAxis1D3 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    override val outputZ: Int,
    private val optimizer: Optimizer.D3,
    private var weight: IOType.D3,
) : Compute.D3() {
    override fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3> {
        TODO("axis1の次元で正規化する")
    }

    override fun train(
        input: Batch<IOType.D3>,
        context: Context,
        calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
    ): Batch<IOType.D3> {
        TODO("axis1の次元で正規化する")
    }
}
