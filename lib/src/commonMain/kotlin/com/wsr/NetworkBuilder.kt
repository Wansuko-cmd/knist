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
        val inputX: Int,
        val inputY: Int,
        override val layers: List<Layer>,
        override val rate: Double,
        override val random: Random,
    ) : NetworkBuilder<I, IOType.D2> {
        fun addLayer(layer: Layer.D2): D2<I> = copy(
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
