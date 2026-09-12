package com.wsr.knist.network.process

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
sealed interface Reshape : Process {
    @Serializable
    abstract class D1ToD2 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI)
        abstract val inputI: Int

        override val outputShape: List<Int> get() = listOf(outputI, outputJ)
        abstract val outputI: Int
        abstract val outputJ: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D2>

        protected abstract fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D1>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(
            input = input as Batch<IOType.D1>,
            env = env,
        )

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D1>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>,
        )
    }

    @Serializable
    abstract class D1ToD3 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI)
        abstract val inputI: Int

        override val outputShape: List<Int> get() = listOf(outputI, outputJ, outputK)
        abstract val outputI: Int
        abstract val outputJ: Int
        abstract val outputK: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D1>, env: GraphEnv): Batch<IOType.D3>

        protected abstract fun IOScope.train(input: Batch<IOType.D1>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D1>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(input = input as Batch<IOType.D1>, env = env)

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D1>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>,
        )
    }

    @Serializable
    abstract class D2ToD1 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI, inputJ)
        abstract val inputI: Int
        abstract val inputJ: Int

        override val outputShape: List<Int> get() = listOf(outputI)
        abstract val outputI: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D1>

        protected abstract fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D2>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(
            input = input as Batch<IOType.D2>,
            env = env,
        )

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D2>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>,
        )
    }

    @Serializable
    abstract class D2ToD3 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI, inputJ)
        abstract val inputI: Int
        abstract val inputJ: Int

        override val outputShape: List<Int> get() = listOf(outputI, outputJ, outputK)
        abstract val outputI: Int
        abstract val outputJ: Int
        abstract val outputK: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D2>, env: GraphEnv): Batch<IOType.D3>

        protected abstract fun IOScope.train(input: Batch<IOType.D2>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): Batch<IOType.D2>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(input = input as Batch<IOType.D2>, env = env)

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D2>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>,
        )
    }

    @Serializable
    abstract class D3ToD1 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI, inputJ, inputK)
        abstract val inputI: Int
        abstract val inputJ: Int
        abstract val inputK: Int

        override val outputShape: List<Int> get() = listOf(outputI)
        abstract val outputI: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D1>

        protected abstract fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): Batch<IOType.D3>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(input = input as Batch<IOType.D3>, env = env)

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D3>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>,
        )
    }

    @Serializable
    abstract class D3ToD2 : Reshape {
        override val inputShape: List<Int> get() = listOf(inputI, inputJ, inputK)
        abstract val inputI: Int
        abstract val inputJ: Int
        abstract val inputK: Int

        override val outputShape: List<Int> get() = listOf(outputI, outputJ)
        abstract val outputI: Int
        abstract val outputJ: Int

        protected abstract fun IOScope.expect(input: Batch<IOType.D3>, env: GraphEnv): Batch<IOType.D2>

        protected abstract fun IOScope.train(input: Batch<IOType.D3>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): Batch<IOType.D3>

        final override fun IOScope._expect(input: Batch<IOType>, env: GraphEnv): Batch<IOType> = expect(input = input as Batch<IOType.D3>, env = env)

        final override fun IOScope._train(input: Batch<IOType>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): Batch<IOType> = train(
            input = input as Batch<IOType.D3>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>,
        )
    }
}
