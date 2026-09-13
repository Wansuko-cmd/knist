package com.wsr.knist.network

import com.wsr.knist.network.converter.Converter
import com.wsr.knist.network.converter.raw.RawD1
import com.wsr.knist.network.converter.raw.RawD2
import com.wsr.knist.network.converter.raw.RawD3
import com.wsr.knist.network.initializer.Fixed
import com.wsr.knist.network.initializer.He
import com.wsr.knist.network.initializer.Random
import com.wsr.knist.network.initializer.WeightInitializer
import com.wsr.knist.network.initializer.Xavier
import com.wsr.knist.network.join.Join
import com.wsr.knist.network.join.add.AddD1
import com.wsr.knist.network.join.add.AddD2
import com.wsr.knist.network.join.add.AddD3
import com.wsr.knist.network.join.concat.ConcatD1
import com.wsr.knist.network.join.concat.ConcatD2
import com.wsr.knist.network.join.concat.ConcatD3
import com.wsr.knist.network.join.max.MaxD1
import com.wsr.knist.network.join.max.MaxD2
import com.wsr.knist.network.join.max.MaxD3
import com.wsr.knist.network.join.mul.MulD1
import com.wsr.knist.network.join.mul.MulD2
import com.wsr.knist.network.join.mul.MulD3
import com.wsr.knist.network.optimizer.Optimizer
import com.wsr.knist.network.optimizer.Scheduler
import com.wsr.knist.network.optimizer.adam.Adam
import com.wsr.knist.network.optimizer.adam.AdamD1
import com.wsr.knist.network.optimizer.adam.AdamD2
import com.wsr.knist.network.optimizer.adam.AdamD3
import com.wsr.knist.network.optimizer.adam.AdamD4
import com.wsr.knist.network.optimizer.adam.AdamW
import com.wsr.knist.network.optimizer.adam.AdamWD1
import com.wsr.knist.network.optimizer.adam.AdamWD2
import com.wsr.knist.network.optimizer.adam.AdamWD3
import com.wsr.knist.network.optimizer.adam.AdamWD4
import com.wsr.knist.network.optimizer.freeze.Freeze
import com.wsr.knist.network.optimizer.freeze.FreezeD1
import com.wsr.knist.network.optimizer.freeze.FreezeD2
import com.wsr.knist.network.optimizer.freeze.FreezeD3
import com.wsr.knist.network.optimizer.freeze.FreezeD4
import com.wsr.knist.network.optimizer.momentum.Momentum
import com.wsr.knist.network.optimizer.momentum.MomentumD1
import com.wsr.knist.network.optimizer.momentum.MomentumD2
import com.wsr.knist.network.optimizer.momentum.MomentumD3
import com.wsr.knist.network.optimizer.momentum.MomentumD4
import com.wsr.knist.network.optimizer.rms.RmsProp
import com.wsr.knist.network.optimizer.rms.RmsPropD1
import com.wsr.knist.network.optimizer.rms.RmsPropD2
import com.wsr.knist.network.optimizer.rms.RmsPropD3
import com.wsr.knist.network.optimizer.rms.RmsPropD4
import com.wsr.knist.network.optimizer.sgd.Sgd
import com.wsr.knist.network.optimizer.sgd.SgdD1
import com.wsr.knist.network.optimizer.sgd.SgdD2
import com.wsr.knist.network.optimizer.sgd.SgdD3
import com.wsr.knist.network.optimizer.sgd.SgdD4
import com.wsr.knist.network.output.Output
import com.wsr.knist.network.output.huber.HuberD1
import com.wsr.knist.network.output.huber.HuberD2
import com.wsr.knist.network.output.huber.HuberD3
import com.wsr.knist.network.output.mean.MeanAbsoluteD1
import com.wsr.knist.network.output.mean.MeanAbsoluteD2
import com.wsr.knist.network.output.mean.MeanAbsoluteD3
import com.wsr.knist.network.output.mean.MeanSquareD1
import com.wsr.knist.network.output.mean.MeanSquareD2
import com.wsr.knist.network.output.mean.MeanSquareD3
import com.wsr.knist.network.output.sigmoid.SigmoidWithLossD1
import com.wsr.knist.network.output.sigmoid.SigmoidWithLossD2
import com.wsr.knist.network.output.sigmoid.SigmoidWithLossD3
import com.wsr.knist.network.output.softmax.SoftmaxWithLossAxisD2
import com.wsr.knist.network.output.softmax.SoftmaxWithLossAxisD3
import com.wsr.knist.network.output.softmax.SoftmaxWithLossD1
import com.wsr.knist.network.output.softmax.SoftmaxWithLossD2
import com.wsr.knist.network.output.softmax.SoftmaxWithLossD3
import com.wsr.knist.network.process.Process
import com.wsr.knist.network.process.compute.affine.AffineD1
import com.wsr.knist.network.process.compute.affine.AffineD2
import com.wsr.knist.network.process.compute.attention.AttentionD2
import com.wsr.knist.network.process.compute.attention.bias.AttentionBiasD2
import com.wsr.knist.network.process.compute.bias.d1.BiasD1
import com.wsr.knist.network.process.compute.bias.d2.BiasAxisD2
import com.wsr.knist.network.process.compute.bias.d2.BiasD2
import com.wsr.knist.network.process.compute.bias.d3.BiasAxisD3
import com.wsr.knist.network.process.compute.bias.d3.BiasD3
import com.wsr.knist.network.process.compute.conv.ConvD1
import com.wsr.knist.network.process.compute.conv.ConvD2
import com.wsr.knist.network.process.compute.debug.DebugD1
import com.wsr.knist.network.process.compute.debug.DebugD2
import com.wsr.knist.network.process.compute.debug.DebugD3
import com.wsr.knist.network.process.compute.dropout.DropoutD1
import com.wsr.knist.network.process.compute.dropout.DropoutD2
import com.wsr.knist.network.process.compute.dropout.DropoutD3
import com.wsr.knist.network.process.compute.function.linear.LinearD1
import com.wsr.knist.network.process.compute.function.linear.LinearD2
import com.wsr.knist.network.process.compute.function.linear.LinearD3
import com.wsr.knist.network.process.compute.function.relu.LeakyReLUD1
import com.wsr.knist.network.process.compute.function.relu.LeakyReLUD2
import com.wsr.knist.network.process.compute.function.relu.LeakyReLUD3
import com.wsr.knist.network.process.compute.function.relu.ReLUD1
import com.wsr.knist.network.process.compute.function.relu.ReLUD2
import com.wsr.knist.network.process.compute.function.relu.ReLUD3
import com.wsr.knist.network.process.compute.function.relu.SwishD1
import com.wsr.knist.network.process.compute.function.relu.SwishD2
import com.wsr.knist.network.process.compute.function.relu.SwishD3
import com.wsr.knist.network.process.compute.function.sigmoid.SigmoidD1
import com.wsr.knist.network.process.compute.function.sigmoid.SigmoidD2
import com.wsr.knist.network.process.compute.function.sigmoid.SigmoidD3
import com.wsr.knist.network.process.compute.function.softmax.SoftmaxAxisD2
import com.wsr.knist.network.process.compute.function.softmax.SoftmaxAxisD3
import com.wsr.knist.network.process.compute.function.softmax.SoftmaxD1
import com.wsr.knist.network.process.compute.function.softmax.SoftmaxD2
import com.wsr.knist.network.process.compute.function.softmax.SoftmaxD3
import com.wsr.knist.network.process.compute.function.tanh.TanhD1
import com.wsr.knist.network.process.compute.function.tanh.TanhD2
import com.wsr.knist.network.process.compute.function.tanh.TanhD3
import com.wsr.knist.network.process.compute.norm.layer.d1.LayerNormD1
import com.wsr.knist.network.process.compute.norm.layer.d2.LayerNormAxisD2
import com.wsr.knist.network.process.compute.norm.layer.d2.LayerNormD2
import com.wsr.knist.network.process.compute.norm.layer.d3.LayerNormAxisD3
import com.wsr.knist.network.process.compute.norm.layer.d3.LayerNormD3
import com.wsr.knist.network.process.compute.norm.minmax.MinMaxNormD1
import com.wsr.knist.network.process.compute.norm.minmax.MinMaxNormD2
import com.wsr.knist.network.process.compute.norm.minmax.MinMaxNormD3
import com.wsr.knist.network.process.compute.norm.rms.d1.RmsNormD1
import com.wsr.knist.network.process.compute.norm.rms.d2.RmsNormAxisD2
import com.wsr.knist.network.process.compute.norm.rms.d2.RmsNormD2
import com.wsr.knist.network.process.compute.norm.rms.d3.RmsNormAxisD3
import com.wsr.knist.network.process.compute.norm.rms.d3.RmsNormD3
import com.wsr.knist.network.process.compute.padding.PaddingD2
import com.wsr.knist.network.process.compute.pool.MaxPoolD2
import com.wsr.knist.network.process.compute.pool.MaxPoolD3
import com.wsr.knist.network.process.compute.position.PositionEmbeddingD2
import com.wsr.knist.network.process.compute.position.PositionEncodeD2
import com.wsr.knist.network.process.compute.position.RoPED2
import com.wsr.knist.network.process.compute.scale.d1.ScaleD1
import com.wsr.knist.network.process.compute.scale.d2.ScaleAxisD2
import com.wsr.knist.network.process.compute.scale.d2.ScaleD2
import com.wsr.knist.network.process.compute.scale.d3.ScaleAxisD3
import com.wsr.knist.network.process.compute.scale.d3.ScaleD3
import com.wsr.knist.network.process.reshape.gad.GlobalAverageD2ToD1
import com.wsr.knist.network.process.reshape.gad.GlobalAverageD3ToD1
import com.wsr.knist.network.process.reshape.gad.GlobalAverageD3ToD2
import com.wsr.knist.network.process.reshape.reshape.ReshapeD1ToD2
import com.wsr.knist.network.process.reshape.reshape.ReshapeD1ToD3
import com.wsr.knist.network.process.reshape.reshape.ReshapeD2ToD1
import com.wsr.knist.network.process.reshape.reshape.ReshapeD2ToD3
import com.wsr.knist.network.process.reshape.reshape.ReshapeD3ToD1
import com.wsr.knist.network.process.reshape.reshape.ReshapeD3ToD2
import com.wsr.knist.network.process.reshape.token.TokenEmbeddingD1ToD2
import kotlin.jvm.JvmName
import kotlin.reflect.KClass
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object NetworkSerializer {
    @JvmName("registerProcess")
    inline fun <reified T : Process> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Process::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerJoin")
    inline fun <reified T : Join> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Join::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOutput")
    inline fun <reified T : Output> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Output::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOptimizer")
    inline fun <reified T : Optimizer> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Optimizer::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerWeightInitializer")
    inline fun <reified T : WeightInitializer> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(WeightInitializer::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOptimizerD1")
    inline fun <reified T : Optimizer.D1> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Optimizer.D1::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOptimizerD2")
    inline fun <reified T : Optimizer.D2> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Optimizer.D2::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOptimizerD3")
    inline fun <reified T : Optimizer.D3> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Optimizer.D3::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerOptimizerD4")
    inline fun <reified T : Optimizer.D4> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Optimizer.D4::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerAttentionBiasD2")
    inline fun <reified T : AttentionBiasD2> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(AttentionBiasD2::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }

    @JvmName("registerConverter")
    inline fun <reified T : Converter<*>> register(clazz: KClass<T>) {
        val module = SerializersModule {
            polymorphic(Converter::class) {
                subclass(clazz)
            }
        }
        networkSerializerModules.add(module)
    }
}

private val buildInSerializersModule = SerializersModule {
    polymorphic(Process::class) {
        // Affine
        subclass(AffineD1::class)
        subclass(AffineD2::class)

        // Attention
        subclass(AttentionD2::class)

        // Bias
        subclass(BiasD1::class)
        subclass(BiasD2::class)
        subclass(BiasAxisD2::class)
        subclass(BiasD3::class)
        subclass(BiasAxisD3::class)

        // Conv
        subclass(ConvD1::class)
        subclass(ConvD2::class)

        // Debug
        subclass(DebugD1::class)
        subclass(DebugD2::class)
        subclass(DebugD3::class)

        // Dropout
        subclass(DropoutD1::class)
        subclass(DropoutD2::class)
        subclass(DropoutD3::class)

        // Function
        subclass(LinearD1::class)
        subclass(LinearD2::class)
        subclass(LinearD3::class)

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
        subclass(SoftmaxAxisD2::class)
        subclass(SoftmaxD3::class)
        subclass(SoftmaxAxisD3::class)

        subclass(TanhD1::class)
        subclass(TanhD2::class)
        subclass(TanhD3::class)

        // Norm
        subclass(LayerNormD1::class)
        subclass(LayerNormD2::class)
        subclass(LayerNormAxisD2::class)
        subclass(LayerNormD3::class)
        subclass(LayerNormAxisD3::class)

        subclass(RmsNormD1::class)
        subclass(RmsNormD2::class)
        subclass(RmsNormAxisD2::class)
        subclass(RmsNormD3::class)
        subclass(RmsNormAxisD3::class)

        subclass(MinMaxNormD1::class)
        subclass(MinMaxNormD2::class)
        subclass(MinMaxNormD3::class)

        // Padding
        subclass(PaddingD2::class)

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

        // Global Average
        subclass(GlobalAverageD2ToD1::class)
        subclass(GlobalAverageD3ToD1::class)
        subclass(GlobalAverageD3ToD2::class)

        // Reshape
        subclass(ReshapeD1ToD2::class)
        subclass(ReshapeD1ToD3::class)
        subclass(ReshapeD2ToD1::class)
        subclass(ReshapeD2ToD3::class)
        subclass(ReshapeD3ToD1::class)
        subclass(ReshapeD3ToD2::class)

        // Token
        subclass(TokenEmbeddingD1ToD2::class)
    }

    polymorphic(Join::class) {
        subclass(AddD1::class)
        subclass(AddD2::class)
        subclass(AddD3::class)

        subclass(ConcatD1::class)
        subclass(ConcatD2::class)
        subclass(ConcatD3::class)

        subclass(MulD1::class)
        subclass(MulD2::class)
        subclass(MulD3::class)

        subclass(MaxD1::class)
        subclass(MaxD2::class)
        subclass(MaxD3::class)
    }

    polymorphic(Output::class) {
        subclass(HuberD1::class)
        subclass(HuberD2::class)
        subclass(HuberD3::class)

        subclass(MeanAbsoluteD1::class)
        subclass(MeanAbsoluteD2::class)
        subclass(MeanAbsoluteD3::class)

        subclass(MeanSquareD1::class)
        subclass(MeanSquareD2::class)
        subclass(MeanSquareD3::class)

        subclass(SigmoidWithLossD1::class)
        subclass(SigmoidWithLossD2::class)
        subclass(SigmoidWithLossD3::class)

        subclass(SoftmaxWithLossD1::class)
        subclass(SoftmaxWithLossD2::class)
        subclass(SoftmaxWithLossAxisD2::class)
        subclass(SoftmaxWithLossD3::class)
        subclass(SoftmaxWithLossAxisD3::class)
    }

    polymorphic(Optimizer::class) {
        subclass(Freeze::class)
        subclass(Sgd::class)
        subclass(Momentum::class)
        subclass(RmsProp::class)
        subclass(Adam::class)
        subclass(AdamW::class)
    }

    polymorphic(Optimizer.D1::class) {
        subclass(FreezeD1::class)
        subclass(SgdD1::class)
        subclass(MomentumD1::class)
        subclass(RmsPropD1::class)
        subclass(AdamD1::class)
        subclass(AdamWD1::class)
    }

    polymorphic(Optimizer.D2::class) {
        subclass(FreezeD2::class)
        subclass(SgdD2::class)
        subclass(MomentumD2::class)
        subclass(RmsPropD2::class)
        subclass(AdamD2::class)
        subclass(AdamWD2::class)
    }

    polymorphic(Optimizer.D3::class) {
        subclass(FreezeD3::class)
        subclass(SgdD3::class)
        subclass(MomentumD3::class)
        subclass(RmsPropD3::class)
        subclass(AdamD3::class)
        subclass(AdamWD3::class)
    }

    polymorphic(Optimizer.D4::class) {
        subclass(FreezeD4::class)
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

    polymorphic(WeightInitializer::class) {
        subclass(He::class)
        subclass(Xavier::class)
        subclass(Random::class)
        subclass(Fixed::class)
    }

    polymorphic(Converter::class) {
        // Raw
        subclass(RawD1::class)
        subclass(RawD2::class)
        subclass(RawD3::class)
    }

    polymorphic(AttentionBiasD2::class) {
        subclass(AttentionBiasD2.Causal::class)
        subclass(AttentionBiasD2.Mask::class)
        subclass(AttentionBiasD2.ALiBi::class)
    }

    polymorphic(Graph.Node::class) {
        subclass(Graph.Node.Attach::class)
        subclass(Graph.Node.Connect::class)
        subclass(Graph.Node.Observe::class)
    }
}

@PublishedApi
internal val networkSerializerModules = mutableListOf(buildInSerializersModule)

internal val networkSerializerJson
    get() = Json {
        serializersModule = networkSerializerModules.reduce { acc, module -> acc + module }
    }

@OptIn(ExperimentalSerializationApi::class)
internal val networkSerializerCbor
    get() = Cbor {
        serializersModule = networkSerializerModules.reduce { acc, module -> acc + module }
    }
