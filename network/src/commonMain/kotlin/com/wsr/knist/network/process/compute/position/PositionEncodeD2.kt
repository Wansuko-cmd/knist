package com.wsr.knist.network.process.compute.position

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.core.d2
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.process.Compute
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class PositionEncodeD2 internal constructor(override val inputI: Int, override val inputJ: Int, private val waveLength: Float, override val id: String = Uuid.random().toString()) : Compute.D2() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    private val position by lazy {
        IOType.d2(outputI, outputJ) { x, y ->
            if (y % 2 == 0) {
                sin(x / waveLength.pow(y / outputJ.toFloat()))
            } else {
                cos(x / waveLength.pow((y - 1) / outputJ.toFloat()))
            }
        }
    }

    override fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D2> = input + position

    override fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D2> {
        val output = input + position
        return calcDelta(output)
    }
}

fun GraphBuilder.Node.D2.positionEncode(waveLength: Float = 10000f, id: String = Uuid.random().toString()) = addCompute(
    compute = PositionEncodeD2(
        inputI = inputI,
        inputJ = inputJ,
        waveLength = waveLength,
        id = id,
    ),
)
