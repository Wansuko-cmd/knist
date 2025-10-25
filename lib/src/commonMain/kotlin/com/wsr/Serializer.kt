package com.wsr

import com.wsr.converter.input.InputConverter
import com.wsr.converter.input.linear.LinearD1
import com.wsr.converter.input.linear.LinearD2
import com.wsr.converter.input.linear.LinearD3
import com.wsr.optimizer.Optimizer
import com.wsr.optimizer.adam.AdamD1
import com.wsr.optimizer.adam.AdamD2
import com.wsr.optimizer.adam.AdamD3
import com.wsr.optimizer.adam.AdamWD1
import com.wsr.optimizer.adam.AdamWD2
import com.wsr.optimizer.adam.AdamWD3
import com.wsr.optimizer.momentum.MomentumD1
import com.wsr.optimizer.momentum.MomentumD2
import com.wsr.optimizer.momentum.MomentumD3
import com.wsr.optimizer.rms.RmsPropD1
import com.wsr.optimizer.rms.RmsPropD2
import com.wsr.optimizer.rms.RmsPropD3
import com.wsr.optimizer.sgd.SgdD1
import com.wsr.optimizer.sgd.SgdD2
import com.wsr.optimizer.sgd.SgdD3
import com.wsr.output.mean.MeanSquareD1
import com.wsr.output.sigmoid.SigmoidWithLossD1
import com.wsr.output.softmax.SoftmaxWithLossD1
import com.wsr.process.affine.AffineD1
import com.wsr.process.affine.AffineD2
import com.wsr.process.bias.BiasD1
import com.wsr.process.bias.BiasD2
import com.wsr.process.bias.BiasD3
import com.wsr.process.conv.ConvD1
import com.wsr.process.dropout.DropoutD1
import com.wsr.process.dropout.DropoutD2
import com.wsr.process.dropout.DropoutD3
import com.wsr.process.function.relu.LeakyReLUD1
import com.wsr.process.function.relu.LeakyReLUD2
import com.wsr.process.function.relu.LeakyReLUD3
import com.wsr.process.function.relu.ReLUD1
import com.wsr.process.function.relu.ReLUD2
import com.wsr.process.function.relu.ReLUD3
import com.wsr.process.function.relu.SwishD1
import com.wsr.process.function.relu.SwishD2
import com.wsr.process.function.relu.SwishD3
import com.wsr.process.function.sigmoid.SigmoidD1
import com.wsr.process.function.sigmoid.SigmoidD2
import com.wsr.process.function.sigmoid.SigmoidD3
import com.wsr.process.function.softmax.SoftmaxD1
import com.wsr.process.function.softmax.SoftmaxD2
import com.wsr.process.function.softmax.SoftmaxD3
import com.wsr.process.norm.layer.LayerNormD1
import com.wsr.process.norm.layer.LayerNormD2
import com.wsr.process.norm.layer.LayerNormD3
import com.wsr.process.norm.minmax.MinMaxNormD1
import com.wsr.process.norm.minmax.MinMaxNormD2
import com.wsr.process.norm.minmax.MinMaxNormD3
import com.wsr.process.pool.MaxPoolD2
import com.wsr.process.pool.MaxPoolD3
import com.wsr.process.skip.SkipD1
import com.wsr.process.skip.SkipD2
import com.wsr.process.skip.SkipD3
import com.wsr.reshape.gad.GlobalAverageD2ToD1
import com.wsr.reshape.gad.GlobalAverageD3ToD1
import com.wsr.reshape.gad.GlobalAverageD3ToD2
import com.wsr.reshape.reshape.ReshapeD2ToD1
import com.wsr.reshape.reshape.ReshapeD3ToD1
import com.wsr.reshape.reshape.ReshapeD3ToD2
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
import com.wsr.process.function.linear.LinearD1 as ProcessLinearD1
import com.wsr.process.function.linear.LinearD2 as ProcessLinearD2
import com.wsr.process.function.linear.LinearD3 as ProcessLinearD3

internal val json =
    Json {
        serializersModule =
            SerializersModule {
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
                    subclass(BiasD3::class)

                    // Conv
                    subclass(ConvD1::class)

                    // Dropout
                    subclass(DropoutD1::class)
                    subclass(DropoutD2::class)
                    subclass(DropoutD3::class)

                    // Function
                    subclass(ProcessLinearD1::class)
                    subclass(ProcessLinearD2::class)
                    subclass(ProcessLinearD3::class)

                    subclass(ReLUD1::class)
                    subclass(ReLUD2::class)
                    subclass(ReLUD3::class)
                    subclass(LeakyReLUD1::class)
                    subclass(LeakyReLUD2::class)
                    subclass(LeakyReLUD3::class)
                    subclass(SwishD1::class)
                    subclass(SwishD2::class)
                    subclass(SwishD3::class)

                    subclass(SigmoidD1::class)
                    subclass(SigmoidD2::class)
                    subclass(SigmoidD3::class)

                    subclass(SoftmaxD1::class)
                    subclass(SoftmaxD2::class)
                    subclass(SoftmaxD3::class)

                    // Norm
                    subclass(LayerNormD1::class)
                    subclass(LayerNormD2::class)
                    subclass(LayerNormD3::class)
                    subclass(MinMaxNormD1::class)
                    subclass(MinMaxNormD2::class)
                    subclass(MinMaxNormD3::class)

                    // Pool
                    subclass(MaxPoolD2::class)
                    subclass(MaxPoolD3::class)

                    // Skip
                    subclass(SkipD1::class)
                    subclass(SkipD2::class)
                    subclass(SkipD3::class)

                    /**
                     * Reshape
                     */
                    subclass(ReshapeD2ToD1::class)
                    subclass(ReshapeD3ToD1::class)
                    subclass(ReshapeD3ToD2::class)
                    subclass(GlobalAverageD2ToD1::class)
                    subclass(GlobalAverageD3ToD1::class)
                    subclass(GlobalAverageD3ToD2::class)

                    /**
                     * Output
                     */
                    subclass(MeanSquareD1::class)
                    subclass(SigmoidWithLossD1::class)
                    subclass(SoftmaxWithLossD1::class)
                }

                /**
                 * Optimizer
                 */
                polymorphic(Optimizer.D1::class) {
                    subclass(SgdD1::class)
                    subclass(MomentumD1::class)
                    subclass(RmsPropD1::class)
                    subclass(AdamD1::class)
                    subclass(AdamWD1::class)
                }

                polymorphic(Optimizer.D2::class) {
                    subclass(SgdD2::class)
                    subclass(MomentumD2::class)
                    subclass(RmsPropD2::class)
                    subclass(AdamD2::class)
                    subclass(AdamWD2::class)
                }

                polymorphic(Optimizer.D3::class) {
                    subclass(SgdD3::class)
                    subclass(MomentumD3::class)
                    subclass(RmsPropD3::class)
                    subclass(AdamD3::class)
                    subclass(AdamWD3::class)
                }

                polymorphic(InputConverter::class) {
                    subclass(LinearD1::class)
                    subclass(LinearD2::class)
                    subclass(LinearD3::class)
                }
            }
    }

internal class NetworkSerializer<I, O : IOType> : KSerializer<Network<I, O>> {
    private val converterSerializer = json.serializersModule.serializer<InputConverter>()
    private val layerSerializer = json.serializersModule.serializer<List<Layer>>()

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(
            serialName = "com.wsr.Network",
            converterSerializer.descriptor,
            layerSerializer.descriptor,
        )

    override fun serialize(encoder: Encoder, value: Network<I, O>) {
        converterSerializer.serialize(encoder, value.converter)
        layerSerializer.serialize(encoder, value.layers)
    }

    override fun deserialize(decoder: Decoder) = Network<I, O>(
        converter = converterSerializer.deserialize(decoder),
        layers = layerSerializer.deserialize(decoder),
    )
}
