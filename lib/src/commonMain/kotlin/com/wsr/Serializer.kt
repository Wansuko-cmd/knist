package com.wsr

import com.wsr.process.affine.AffineD1
import com.wsr.process.affine.AffineD2
import com.wsr.process.bias.BiasD1
import com.wsr.process.bias.BiasD2
import com.wsr.process.conv.ConvD1
import com.wsr.process.dropout.DropoutD1
import com.wsr.process.dropout.DropoutD2
import com.wsr.process.function.linear.LinearD1
import com.wsr.process.function.linear.LinearD2
import com.wsr.process.function.relu.LeakyReLUD1
import com.wsr.process.function.relu.LeakyReLUD2
import com.wsr.process.function.relu.ReLUD1
import com.wsr.process.function.relu.ReLUD2
import com.wsr.process.function.relu.SwishD1
import com.wsr.process.function.relu.SwishD2
import com.wsr.process.function.sigmoid.SigmoidD1
import com.wsr.process.function.sigmoid.SigmoidD2
import com.wsr.process.function.softmax.SoftmaxD1
import com.wsr.process.norm.MinMaxNormD1
import com.wsr.process.pool.MaxPoolD2
import com.wsr.reshape.ReshapeD2ToD1
import com.wsr.output.mean.MeanSquareD1
import com.wsr.output.sigmoid.SigmoidWithLossD1
import com.wsr.output.softmax.SoftmaxWithLossD1
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
            /**
             * Process
             */
            // Affine
            subclass(AffineD1::class)
            subclass(AffineD2::class)

            // Bias
            subclass(BiasD1::class)
            subclass(BiasD2::class)

            // Conv
            subclass(ConvD1::class)

            // Dropout
            subclass(DropoutD1::class)
            subclass(DropoutD2::class)

            // Function
            subclass(LinearD1::class)
            subclass(LinearD2::class)

            subclass(ReLUD1::class)
            subclass(ReLUD2::class)
            subclass(LeakyReLUD1::class)
            subclass(LeakyReLUD2::class)
            subclass(SwishD1::class)
            subclass(SwishD2::class)

            subclass(SigmoidD1::class)
            subclass(SigmoidD2::class)

            subclass(SoftmaxD1::class)

            // Norm
            subclass(MinMaxNormD1::class)

            // Pool
            subclass(MaxPoolD2::class)

            /**
             * Reshape
             */
            subclass(ReshapeD2ToD1::class)

            /**
             * Output
             */
            subclass(MeanSquareD1::class)
            subclass(SigmoidWithLossD1::class)
            subclass(SoftmaxWithLossD1::class)
        }
    }
}

internal class NetworkSerializer<I : IOType, O : IOType>() : KSerializer<Network<I, O>> {
    private val layerSerializer = json.serializersModule.serializer<List<Layer>>()

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "com.wsr.Network",
        layerSerializer.descriptor,
    )

    override fun serialize(encoder: Encoder, value: Network<I, O>) {
        layerSerializer.serialize(encoder, value.layers)
    }

    override fun deserialize(decoder: Decoder) =
        Network<I, O>(layerSerializer.deserialize(decoder))
}
