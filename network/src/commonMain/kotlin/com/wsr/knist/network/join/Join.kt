package com.wsr.knist.network.join

import com.wsr.knist.batch.Batch
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import com.wsr.knist.network.GraphEnv
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
sealed interface Join {
    @Suppress("FunctionName")
    fun IOScope._expect(inputs: List<Batch<IOType>>, env: GraphEnv): Batch<IOType>

    @Suppress("FunctionName")
    fun IOScope._train(inputs: List<Batch<IOType>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): List<Batch<IOType>>

    @Serializable
    abstract class D1 : Join {
        abstract val outputI: Int

        protected abstract fun IOScope.expect(inputs: List<Batch<IOType.D1>>, env: GraphEnv): Batch<IOType.D1>

        protected abstract fun IOScope.train(inputs: List<Batch<IOType.D1>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>): List<Batch<IOType.D1>>

        final override fun IOScope._expect(inputs: List<Batch<IOType>>, env: GraphEnv): Batch<IOType> = expect(
            inputs = inputs as List<Batch<IOType.D1>>,
            env = env,
        )

        final override fun IOScope._train(inputs: List<Batch<IOType>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): List<Batch<IOType>> = train(
            inputs = inputs as List<Batch<IOType.D1>>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D1>) -> Batch<IOType.D1>,
        )
    }

    @Serializable
    abstract class D2 : Join {
        abstract val outputI: Int
        abstract val outputJ: Int

        protected abstract fun IOScope.expect(inputs: List<Batch<IOType.D2>>, env: GraphEnv): Batch<IOType.D2>

        protected abstract fun IOScope.train(inputs: List<Batch<IOType.D2>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>): List<Batch<IOType.D2>>

        final override fun IOScope._expect(inputs: List<Batch<IOType>>, env: GraphEnv): Batch<IOType> = expect(
            inputs = inputs as List<Batch<IOType.D2>>,
            env = env,
        )

        final override fun IOScope._train(inputs: List<Batch<IOType>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): List<Batch<IOType>> = train(
            inputs = inputs as List<Batch<IOType.D2>>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D2>) -> Batch<IOType.D2>,
        )
    }

    @Serializable
    abstract class D3 : Join {
        abstract val outputI: Int
        abstract val outputJ: Int
        abstract val outputK: Int

        protected abstract fun IOScope.expect(inputs: List<Batch<IOType.D3>>, env: GraphEnv): Batch<IOType.D3>

        protected abstract fun IOScope.train(inputs: List<Batch<IOType.D3>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>): List<Batch<IOType.D3>>

        final override fun IOScope._expect(inputs: List<Batch<IOType>>, env: GraphEnv): Batch<IOType> = expect(
            inputs = inputs as List<Batch<IOType.D3>>,
            env = env,
        )

        final override fun IOScope._train(inputs: List<Batch<IOType>>, env: GraphEnv, calcDelta: IOScope.(Batch<IOType>) -> Batch<IOType>): List<Batch<IOType>> = train(
            inputs = inputs as List<Batch<IOType.D3>>,
            env = env,
            calcDelta = calcDelta as IOScope.(Batch<IOType.D3>) -> Batch<IOType.D3>,
        )
    }
}
