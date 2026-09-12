package com.wsr.knist.network.process.compute.scale.d3

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphBuilder
import com.wsr.knist.network.GraphEnv
import com.wsr.knist.network.GraphScope.addCompute
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.process.Compute
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable

@Serializable
class ScaleD3 internal constructor(
    override val inputI: Int,
    override val inputJ: Int,
    override val inputK: Int,
    private var optimizer: Optimizer.D3,
    private var weight: IOType.D3.Global,
    override val id: String = Uuid.random().toString(),
) : Compute.D3() {
    override val outputI: Int get() = inputI
    override val outputJ: Int get() = inputJ
    override val outputK: Int get() = inputK
    override fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D3> = input * weight

    override fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D3> {
        val output = input * weight
        val delta = calcDelta(output)

        val dx = delta * weight
        weight = optimizer.adapt(
            weight = weight,
            dw = input * delta,
        ).toGlobal()

        return dx
    }

    override fun update(optimizer: Optimizer) {
        this.optimizer = optimizer.d3(i = inputI, j = inputJ, k = inputK)
    }
}

fun GraphBuilder.Node.D3.scale(axis: Int? = null, optimizer: Optimizer = this.optimizer, initializer: WeightInitializer = Fixed(1f), id: String = Uuid.random().toString()): GraphBuilder.Node.D3 {
    val process = when (axis) {
        null -> ScaleD3(
            inputI = inputI,
            inputJ = inputJ,
            inputK = inputK,
            optimizer = optimizer.d3(
                inputI,
                inputJ,
                inputK,
            ),
            weight = initializer.d3(
                input = listOf(inputI, inputJ, inputK),
                output = listOf(inputI, inputJ, inputK),
                i = inputI,
                j = inputJ,
                k = inputK,
            ),
            id = id,
        )

        0, 1, 2 -> {
            val inputT = when (axis) {
                0 -> inputI
                1 -> inputJ
                else -> inputK
            }
            ScaleAxisD3(
                inputI = inputI,
                inputJ = inputJ,
                inputK = inputK,
                axis = axis,
                optimizer = optimizer.d1(inputT),
                weight = initializer.d1(
                    input = listOf(inputT),
                    output = listOf(inputT),
                    size = inputT,
                ),
                id = id,
            )
        }

        else -> throw IllegalStateException(
            """
            invalid parameter.
            axis: $axis
            """.trimIndent(),
        )
    }
    return addCompute(compute = process)
}
