package com.wsr

import com.wsr.layers.Process
import com.wsr.layers.reshape.ReshapeD2ToD1
import com.wsr.output.Output
import kotlin.random.Random

sealed interface NetworkBuilder<I : IOType, O : IOType> {
    val layers: List<Process>
    val rate: Double
    val random: Random

    @ConsistentCopyVisibility
    data class D1<I : IOType> internal constructor(
        val inputSize: Int,
        override val layers: List<Process>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addProcess(layer: Process.D1): D1<I> = copy(
            layers = layers + layer,
            inputSize = layer.outputSize,
        )
        fun addOutput(output: Output.D1) = Network<I, IOType.D1>(layers, output)
    }

    @ConsistentCopyVisibility
    data class D2<I : IOType> internal constructor(
        val inputX: Int,
        val inputY: Int,
        override val layers: List<Process>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addProcess(layer: Process.D2): D2<I> = copy(
            layers = layers + layer,
            inputX = layer.outputX,
            inputY = layer.outputY,
        )

        fun reshapeD1(): D1<I> {
            val reshape = ReshapeD2ToD1(inputX, inputY)
            return D1(
                layers = layers + reshape,
                inputSize = reshape.outputSize,
                rate = rate,
                random = random,
            )
        }
    }

    companion object {
        fun inputD1(
            inputSize: Int,
            rate: Double,
            seed: Int? = null,
        ) = D1<IOType.D1>(
            inputSize = inputSize,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )

        fun inputD2(
            x: Int,
            y: Int,
            rate: Double,
            seed: Int? = null,
        ) = D2<IOType.D2>(
            inputX = x,
            inputY = y,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )
    }
}
