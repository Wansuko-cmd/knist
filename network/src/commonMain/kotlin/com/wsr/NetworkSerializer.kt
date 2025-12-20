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
import com.wsr.optimizer.Optimizer
import com.wsr.optimizer.Scheduler
import com.wsr.optimizer.adam.AdamD1
import com.wsr.optimizer.adam.AdamD2
import com.wsr.optimizer.adam.AdamD3
import com.wsr.optimizer.adam.AdamD4
import com.wsr.optimizer.adam.AdamWD1
import com.wsr.optimizer.adam.AdamWD2
import com.wsr.optimizer.adam.AdamWD3
import com.wsr.optimizer.adam.AdamWD4
import com.wsr.optimizer.momentum.MomentumD1
import com.wsr.optimizer.momentum.MomentumD2
import com.wsr.optimizer.momentum.MomentumD3
import com.wsr.optimizer.momentum.MomentumD4
import com.wsr.optimizer.rms.RmsPropD1
import com.wsr.optimizer.rms.RmsPropD2
import com.wsr.optimizer.rms.RmsPropD3
import com.wsr.optimizer.rms.RmsPropD4
import com.wsr.optimizer.sgd.SgdD1
import com.wsr.optimizer.sgd.SgdD2
import com.wsr.optimizer.sgd.SgdD3
import com.wsr.optimizer.sgd.SgdD4
import com.wsr.output.Output
import com.wsr.output.mean.MeanSquareD1
import com.wsr.output.mean.MeanSquareD2
import com.wsr.output.sigmoid.SigmoidWithLossD1
import com.wsr.output.sigmoid.SigmoidWithLossD2
import com.wsr.output.softmax.SoftmaxWithLossD1
import com.wsr.output.softmax.SoftmaxWithLossD2
import com.wsr.process.Process
import com.wsr.process.compute.affine.AffineD1
import com.wsr.process.compute.affine.AffineD2
import com.wsr.process.compute.attention.AttentionD2
import com.wsr.process.compute.bias.BiasD1
import com.wsr.process.compute.bias.BiasD2
import com.wsr.process.compute.bias.BiasD3
import com.wsr.process.compute.conv.ConvD1
import com.wsr.process.compute.debug.DebugD1
import com.wsr.process.compute.debug.DebugD2
import com.wsr.process.compute.debug.DebugD3
import com.wsr.process.compute.dropout.DropoutD1
import com.wsr.process.compute.dropout.DropoutD2
import com.wsr.process.compute.dropout.DropoutD3
import com.wsr.process.compute.function.linear.LinearD1 as ProcessLinearD1
import com.wsr.process.compute.function.linear.LinearD2 as ProcessLinearD2
import com.wsr.process.compute.function.linear.LinearD3 as ProcessLinearD3
import com.wsr.process.compute.function.relu.LeakyReLUD1
import com.wsr.process.compute.function.relu.LeakyReLUD2
import com.wsr.process.compute.function.relu.LeakyReLUD3
import com.wsr.process.compute.function.relu.ReLUD1
import com.wsr.process.compute.function.relu.ReLUD2
import com.wsr.process.compute.function.relu.ReLUD3
import com.wsr.process.compute.function.relu.SwishD1
import com.wsr.process.compute.function.relu.SwishD2
import com.wsr.process.compute.function.relu.SwishD3
import com.wsr.process.compute.function.sigmoid.SigmoidD1
import com.wsr.process.compute.function.sigmoid.SigmoidD2
import com.wsr.process.compute.function.sigmoid.SigmoidD3
import com.wsr.process.compute.function.softmax.SoftmaxD1
import com.wsr.process.compute.function.softmax.SoftmaxD2
import com.wsr.process.compute.function.softmax.SoftmaxD3
import com.wsr.process.compute.norm.layer.d1.LayerNormD1
import com.wsr.process.compute.norm.layer.d2.LayerNormAxisD2
import com.wsr.process.compute.norm.layer.d2.LayerNormD2
import com.wsr.process.compute.norm.layer.d3.LayerNormAxisD3
import com.wsr.process.compute.norm.layer.d3.LayerNormD3
import com.wsr.process.compute.norm.minmax.MinMaxNormD1
import com.wsr.process.compute.norm.minmax.MinMaxNormD2
import com.wsr.process.compute.norm.minmax.MinMaxNormD3
import com.wsr.process.compute.pool.MaxPoolD2
import com.wsr.process.compute.pool.MaxPoolD3
import com.wsr.process.compute.position.PositionEmbeddingD2
import com.wsr.process.compute.position.PositionEncodeD2
import com.wsr.process.compute.position.RoPED2
import com.wsr.process.compute.scale.d1.ScaleD1
import com.wsr.process.compute.scale.d2.ScaleAxisD2
import com.wsr.process.compute.scale.d2.ScaleD2
import com.wsr.process.compute.scale.d3.ScaleAxisD3
import com.wsr.process.compute.scale.d3.ScaleD3
import com.wsr.process.compute.skip.SkipD1
import com.wsr.process.compute.skip.SkipD2
import com.wsr.process.compute.skip.SkipD3
import com.wsr.process.reshape.gad.GlobalAverageD2ToD1
import com.wsr.process.reshape.gad.GlobalAverageD3ToD1
import com.wsr.process.reshape.gad.GlobalAverageD3ToD2
import com.wsr.process.reshape.reshape.ReshapeD2ToD1
import com.wsr.process.reshape.reshape.ReshapeD3ToD1
import com.wsr.process.reshape.reshape.ReshapeD3ToD2
import com.wsr.process.reshape.token.TokenEmbeddingD1ToD2
import kotlin.reflect.KClass
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

class NetworkSerializer<I, O> : KSerializer<Network<I, O>> {
    private val converterSerializer = json.serializersModule.serializer<Converter>()
    private val layerSerializer = json.serializersModule.serializer<List<Process>>()
    private val outputSerializer = json.serializersModule.serializer<Output>()

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(
            serialName = "com.wsr.Network",
            converterSerializer.descriptor,
            layerSerializer.descriptor,
            outputSerializer.descriptor,
        )

    override fun serialize(encoder: Encoder, value: Network<I, O>) {
        converterSerializer.serialize(encoder, value.inputConverter)
        converterSerializer.serialize(encoder, value.outputConverter)
        layerSerializer.serialize(encoder, value.layers)
        outputSerializer.serialize(encoder, value.output)
    }

    override fun deserialize(decoder: Decoder) = Network<I, O>(
        inputConverter = converterSerializer.deserialize(decoder),
        outputConverter = converterSerializer.deserialize(decoder),
        layers = layerSerializer.deserialize(decoder),
        output = outputSerializer.deserialize(decoder),
    )

    companion object {
        val modules = mutableListOf(buildInSerializersModule)

        @JvmName("registerLayer")
        inline fun <reified T : Process> register(clazz: KClass<T>) {
            val module = SerializersModule {
                polymorphic(Process::class) {
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
    polymorphic(Process::class) {
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
        subclass(LayerNormAxisD2::class)

        subclass(LayerNormD3::class)
        subclass(LayerNormAxisD3::class)

        subclass(MinMaxNormD1::class)
        subclass(MinMaxNormD2::class)
        subclass(MinMaxNormD3::class)

        // Pool
        subclass(MaxPoolD2::class)
        subclass(MaxPoolD3::class)

        // Position
        subclass(PositionEncodeD2::class)
        subclass(PositionEmbeddingD2::class)
        subclass(RoPED2::class)

        // Scale
        subclass(ScaleD1::class)
        subclass(ScaleD2::class)
        subclass(ScaleAxisD2::class)
        subclass(ScaleD3::class)
        subclass(ScaleAxisD3::class)

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
    }

    polymorphic(Output::class) {
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

    polymorphic(Optimizer.D4::class) {
        subclass(SgdD4::class)
        subclass(MomentumD4::class)
        subclass(RmsPropD4::class)
        subclass(AdamD4::class)
        subclass(AdamWD4::class)
    }

    polymorphic(Scheduler::class) {
        subclass(Scheduler.Fix::class)
        subclass(Scheduler.Step::class)
        subclass(Scheduler.MultiStep::class)
        subclass(Scheduler.CosineAnnealing::class)
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
