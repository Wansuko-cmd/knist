package com.wsr

import com.wsr.converter.Converter
import com.wsr.converter.char.CharD1
import com.wsr.converter.char.CharsD1
import com.wsr.converter.linear.LinearD1
import com.wsr.converter.linear.LinearD2
import com.wsr.converter.linear.LinearD3
import com.wsr.converter.word.WordD1
import com.wsr.converter.word.WordD2
import com.wsr.converter.word.WordsD1
import com.wsr.layer.Layer
import com.wsr.layer.output.mean.MeanSquareD1
import com.wsr.layer.output.mean.MeanSquareD2
import com.wsr.layer.output.sigmoid.SigmoidWithLossD1
import com.wsr.layer.output.sigmoid.SigmoidWithLossD2
import com.wsr.layer.output.softmax.SoftmaxWithLossD1
import com.wsr.layer.output.softmax.SoftmaxWithLossD2
import com.wsr.layer.process.affine.AffineD1
import com.wsr.layer.process.affine.AffineD2
import com.wsr.layer.process.attention.AttentionD2
import com.wsr.layer.process.bias.BiasD1
import com.wsr.layer.process.bias.BiasD2
import com.wsr.layer.process.bias.BiasD3
import com.wsr.layer.process.conv.ConvD1
import com.wsr.layer.process.debug.DebugD1
import com.wsr.layer.process.debug.DebugD2
import com.wsr.layer.process.debug.DebugD3
import com.wsr.layer.process.dropout.DropoutD1
import com.wsr.layer.process.dropout.DropoutD2
import com.wsr.layer.process.dropout.DropoutD3
import com.wsr.layer.process.function.relu.LeakyReLUD1
import com.wsr.layer.process.function.relu.LeakyReLUD2
import com.wsr.layer.process.function.relu.LeakyReLUD3
import com.wsr.layer.process.function.relu.ReLUD1
import com.wsr.layer.process.function.relu.ReLUD2
import com.wsr.layer.process.function.relu.ReLUD3
import com.wsr.layer.process.function.relu.SwishD1
import com.wsr.layer.process.function.relu.SwishD2
import com.wsr.layer.process.function.relu.SwishD3
import com.wsr.layer.process.function.sigmoid.SigmoidD1
import com.wsr.layer.process.function.sigmoid.SigmoidD2
import com.wsr.layer.process.function.sigmoid.SigmoidD3
import com.wsr.layer.process.function.softmax.SoftmaxD1
import com.wsr.layer.process.function.softmax.SoftmaxD2
import com.wsr.layer.process.function.softmax.SoftmaxD3
import com.wsr.layer.process.norm.layer.d1.LayerNormD1
import com.wsr.layer.process.norm.layer.d2.LayerNormAxis0D2
import com.wsr.layer.process.norm.layer.d2.LayerNormAxis1D2
import com.wsr.layer.process.norm.layer.d2.LayerNormD2
import com.wsr.layer.process.norm.layer.d3.LayerNormAxis0D3
import com.wsr.layer.process.norm.layer.d3.LayerNormAxis1D3
import com.wsr.layer.process.norm.layer.d3.LayerNormAxis2D3
import com.wsr.layer.process.norm.layer.d3.LayerNormD3
import com.wsr.layer.process.norm.minmax.MinMaxNormD1
import com.wsr.layer.process.norm.minmax.MinMaxNormD2
import com.wsr.layer.process.norm.minmax.MinMaxNormD3
import com.wsr.layer.process.pool.MaxPoolD2
import com.wsr.layer.process.pool.MaxPoolD3
import com.wsr.layer.process.position.PositionEmbeddingD2
import com.wsr.layer.process.position.PositionEncodeD2
import com.wsr.layer.process.position.RoPED2
import com.wsr.layer.process.skip.SkipD1
import com.wsr.layer.process.skip.SkipD2
import com.wsr.layer.process.skip.SkipD3
import com.wsr.layer.reshape.gad.GlobalAverageD2ToD1
import com.wsr.layer.reshape.gad.GlobalAverageD3ToD1
import com.wsr.layer.reshape.gad.GlobalAverageD3ToD2
import com.wsr.layer.reshape.reshape.ReshapeD2ToD1
import com.wsr.layer.reshape.reshape.ReshapeD3ToD1
import com.wsr.layer.reshape.reshape.ReshapeD3ToD2
import com.wsr.layer.reshape.token.TokenEmbeddingD1ToD2
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.okio.decodeFromBufferedSource
import kotlinx.serialization.json.okio.encodeToBufferedSink
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import okio.BufferedSink
import okio.BufferedSource
import kotlin.reflect.KClass
import com.wsr.layer.process.function.linear.LinearD1 as ProcessLinearD1
import com.wsr.layer.process.function.linear.LinearD2 as ProcessLinearD2
import com.wsr.layer.process.function.linear.LinearD3 as ProcessLinearD3

class NetworkSerializer<I, O> : KSerializer<Network<I, O>> {
    private val converterSerializer = json.serializersModule.serializer<Converter>()
    private val layerSerializer = json.serializersModule.serializer<List<Layer>>()

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(
            serialName = "com.wsr.Network",
            converterSerializer.descriptor,
            layerSerializer.descriptor,
        )

    override fun serialize(encoder: Encoder, value: Network<I, O>) {
        converterSerializer.serialize(encoder, value.inputConverter)
        converterSerializer.serialize(encoder, value.outputConverter)
        layerSerializer.serialize(encoder, value.layers)
    }

    override fun deserialize(decoder: Decoder) = Network<I, O>(
        inputConverter = converterSerializer.deserialize(decoder),
        outputConverter = converterSerializer.deserialize(decoder),
        layers = layerSerializer.deserialize(decoder),
    )

    companion object {
        val modules = mutableListOf(buildInSerializersModule)

        @JvmName("registerLayer")
        inline fun <reified T : Layer> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Layer::class) {
                    subclass(clazz)
                }
            }
            modules.add(module)
        }

        @JvmName("registerOptimizerD1")
        inline fun <reified T : Optimizer.D1> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Optimizer.D1::class) {
                    subclass(clazz)
                }
            }
            modules.add(module)
        }

        @JvmName("registerOptimizerD2")
        inline fun <reified T : Optimizer.D2> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Optimizer.D2::class) {
                    subclass(clazz)
                }
            }
            modules.add(module)
        }

        @JvmName("registerOptimizerD3")
        inline fun <reified T : Optimizer.D3> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Optimizer.D3::class) {
                    subclass(clazz)
                }
            }
            modules.add(module)
        }

        @JvmName("registerConverter")
        inline fun <reified T : Converter> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Converter::class) {
                    subclass(clazz)
                }
            }
            modules.add(module)
        }

        fun <I, O> encodeToString(value: Network<I, O>) = json.encodeToString(
            serializer = NetworkSerializer(),
            value = value,
        )

        @OptIn(ExperimentalSerializationApi::class)
        fun <I, O> encodeToBufferedSink(value: Network<I, O>, sink: BufferedSink) = json.encodeToBufferedSink(
            serializer = NetworkSerializer(),
            value = value,
            sink = sink,
        )

        fun <I, O> decodeFromString(value: String) = json.decodeFromString<Network<I, O>>(
            deserializer = NetworkSerializer(),
            string = value,
        )

        @OptIn(ExperimentalSerializationApi::class)
        fun <I, O> decodeFromBufferedSource(source: BufferedSource) = json.decodeFromBufferedSource<Network<I, O>>(
            deserializer = NetworkSerializer(),
            source = source,
        )

        private val json
            get() = Json {
                serializersModule = modules.reduce { acc, module -> acc + module }
            }
    }
}

private val buildInSerializersModule = SerializersModule {
    polymorphic(Layer::class) {
        /**
         * Process
         */
        // Affine
        subclass(AffineD1::class)
        subclass(AffineD2::class)

        // Attention
        subclass(AttentionD2::class)

        // Bias
        subclass(BiasD1::class)
        subclass(BiasD2::class)
        subclass(BiasD3::class)

        // Conv
        subclass(ConvD1::class)

        // Debug
        subclass(DebugD1::class)
        subclass(DebugD2::class)
        subclass(DebugD3::class)

        // Dropout
        subclass(DropoutD1::class)
        subclass(DropoutD2::class)
        subclass(DropoutD3::class)

        // Position
        subclass(PositionEncodeD2::class)
        subclass(PositionEmbeddingD2::class)
        subclass(RoPED2::class)

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
        subclass(LayerNormAxis0D2::class)
        subclass(LayerNormAxis1D2::class)

        subclass(LayerNormD3::class)
        subclass(LayerNormAxis0D3::class)
        subclass(LayerNormAxis1D3::class)
        subclass(LayerNormAxis2D3::class)

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
        // Global Average
        subclass(GlobalAverageD2ToD1::class)
        subclass(GlobalAverageD3ToD1::class)
        subclass(GlobalAverageD3ToD2::class)

        // Reshape
        subclass(ReshapeD2ToD1::class)
        subclass(ReshapeD3ToD1::class)
        subclass(ReshapeD3ToD2::class)

        // Token
        subclass(TokenEmbeddingD1ToD2::class)

        /**
         * Output
         */
        subclass(MeanSquareD1::class)
        subclass(MeanSquareD2::class)

        subclass(SigmoidWithLossD1::class)
        subclass(SigmoidWithLossD2::class)

        subclass(SoftmaxWithLossD1::class)
        subclass(SoftmaxWithLossD2::class)
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

    polymorphic(Converter::class) {
        // Char
        subclass(CharD1::class)
        subclass(CharsD1::class)

        // Linear
        subclass(LinearD1::class)
        subclass(LinearD2::class)
        subclass(LinearD3::class)

        // Word
        subclass(WordD1::class)
        subclass(WordD2::class)
        subclass(WordsD1::class)
    }
}
