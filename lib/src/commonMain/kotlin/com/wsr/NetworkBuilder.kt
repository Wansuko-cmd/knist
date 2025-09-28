package com.wsr

import com.wsr.layers.Process
import com.wsr.output.Output
import com.wsr.reshape.Reshape
import kotlin.random.Random

sealed interface NetworkBuilder<I : IOType, O : IOType> {
    val layers: List<Layer>
    val rate: Double
    val random: Random

    @ConsistentCopyVisibility
    data class D1<I : IOType> internal constructor(
        val inputSize: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addProcess(process: Process.D1): D1<I> = copy(
            layers = layers + process,
            inputSize = process.outputSize,
        )

        fun addOutput(output: Output.D1) = Network<I, IOType.D1>(layers + output)
    }

    @ConsistentCopyVisibility
    data class D2<I : IOType> internal constructor(
        val inputX: Int,
        val inputY: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addProcess(process: Process.D2): D2<I> = copy(
            layers = layers + process,
            inputX = process.outputX,
            inputY = process.outputY,
        )

        fun addReshape(reshape: Reshape.D2ToD1): D1<I> = D1(
            layers = layers + reshape,
            inputSize = reshape.outputSize,
            rate = rate,
            random = random,
        )
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
