package com.wsr

import com.wsr.common.IOType
import com.wsr.layers.Layer
import com.wsr.layers.reshape.ReshapeD2ToD1
import kotlin.random.Random

sealed interface NetworkBuilder<I : IOType, O : IOType> {
    val layers: List<Layer>
    val rate: Double
    val random: Random

    fun build() = Network<I, O>(layers = layers)

    @ConsistentCopyVisibility
    data class D1<I : IOType> internal constructor(
        val inputSize: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D1> {
        fun addLayer(layer: Layer.D1): D1<I> = copy(
            layers = layers + layer,
            inputSize = layer.outputSize,
        )
    }

    @ConsistentCopyVisibility
    data class D2<I : IOType> internal constructor(
        val x: Int,
        val y: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addLayer(layer: Layer.D2): D2<I> = copy(
            layers = layers + layer,
            x = layer.outputShape[0],
            y = layer.outputShape[1],
        )

        fun reshapeD1(): D1<I> {
            val numOfInput = x * y
            return D1(
                inputSize = numOfInput,
                layers = layers + ReshapeD2ToD1(listOf(x, y)),
                rate = rate,
                random = random,
            )
        }
    }

    companion object {
        fun inputD1(
            numOfInput: Int,
            rate: Double,
            seed: Int? = null,
        ) = D1<IOType.D1>(
            inputSize = numOfInput,
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
            x = x,
            y = y,
            rate = rate,
            random = seed?.let { Random(it) } ?: Random,
            layers = emptyList(),
        )
    }
}
