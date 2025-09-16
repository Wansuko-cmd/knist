package com.wsr

import com.wsr.layers.Layer
import com.wsr.layers.affine.AffineD1
import com.wsr.layers.affine.AffineD2
import com.wsr.layers.bias.BiasD1
import com.wsr.layers.bias.BiasD2
import com.wsr.layers.conv.ConvD1
import com.wsr.layers.function.linear.LinearD1
import com.wsr.layers.function.linear.LinearD2
import com.wsr.layers.function.relu.ReluD1
import com.wsr.layers.function.relu.ReluD2
import com.wsr.layers.function.sigmoid.SigmoidD1
import com.wsr.layers.function.softmax.SoftmaxD1
import com.wsr.layers.pool.MaxPoolD1
import com.wsr.layers.reshape.ReshapeD2ToD1
import com.wsr.output.Output
import com.wsr.output.SigmoidWithLoss
import com.wsr.output.SoftmaxWithLoss
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer

internal val json = Json {
    serializersModule = SerializersModule {
        polymorphic(Layer::class) {
            // Affine
            subclass(AffineD1::class)
            subclass(AffineD2::class)

            // Bias
            subclass(BiasD1::class)
            subclass(BiasD2::class)

            // Conv
            subclass(ConvD1::class)

            // Function
            subclass(LinearD1::class)
            subclass(LinearD2::class)

            subclass(ReluD1::class)
            subclass(ReluD2::class)

            subclass(SigmoidD1::class)

            subclass(SoftmaxD1::class)

            // Pool
            subclass(MaxPoolD1::class)

            // Reshape
            subclass(ReshapeD2ToD1::class)
        }

        polymorphic(Output::class) {
            subclass(SigmoidWithLoss::class)
            subclass(SoftmaxWithLoss::class)
        }
    }
}

internal class NetworkSerializer<I : IOType, O : IOType>() : KSerializer<Network<I, O>> {
    private val layerSerializer = json.serializersModule.serializer<List<Layer>>()
    private val outputSerializer = json.serializersModule.serializer<Output>()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "com.wsr.Network",
        layerSerializer.descriptor,
        outputSerializer.descriptor,
    )

    override fun serialize(encoder: Encoder, value: Network<I, O>) {
        layerSerializer.serialize(encoder, value.layers)
        outputSerializer.serialize(encoder, value.output)
    }

    override fun deserialize(decoder: Decoder) =
        Network<I, O>(layerSerializer.deserialize(decoder), outputSerializer.deserialize(decoder))
}
