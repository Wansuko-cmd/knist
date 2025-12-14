package com.wsr.layer.compute

import com.wsr.batch.Batch
import com.wsr.core.IOType
import com.wsr.layer.Context
import com.wsr.layer.Layer
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
@Serializable
sealed interface Compute : Layer {
    @Serializable
    abstract class D1 : Compute {
        abstract val outputSize: Int

        protected abstract fun expect(input: Batch<IOType.D1>, context: Context): Batch<IOType.D1>

        protected abstract fun train(
            input: Batch<IOType.D1>,
            context: Context,
            calcDelta: (Batch<IOType.D1>) -> Batch<IOType.D1>,
        ): Batch<IOType.D1>

        final override fun _expect(input: Batch<IOType>, context: Context): Batch<IOType> = expect(
            input = input as Batch<IOType.D1>,
            context = context,
        )

        final override fun _train(
            input: Batch<IOType>,
            context: Context,
            calcDelta: (Batch<IOType>) -> Batch<IOType>,
        ): Batch<IOType> = train(
            input = input as Batch<IOType.D1>,
            context = context,
            calcDelta = { input: Batch<IOType.D1> -> calcDelta(input) as Batch<IOType.D1> },
        )
    }

    @Serializable
    abstract class D2 : Compute {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2>

        protected abstract fun train(
            input: Batch<IOType.D2>,
            context: Context,
            calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
        ): Batch<IOType.D2>

        final override fun _expect(input: Batch<IOType>, context: Context): Batch<IOType> = expect(
            input = input as Batch<IOType.D2>,
            context = context,
        )

        final override fun _train(
            input: Batch<IOType>,
            context: Context,
            calcDelta: (Batch<IOType>) -> Batch<IOType>,
        ): Batch<IOType> = train(
            input = input as Batch<IOType.D2>,
            context = context,
            calcDelta = { input: Batch<IOType.D2> -> calcDelta(input) as Batch<IOType.D2> },
        )
    }

    @Serializable
    abstract class D3 : Compute {
        abstract val outputX: Int
        abstract val outputY: Int
        abstract val outputZ: Int

        protected abstract fun expect(input: Batch<IOType.D3>, context: Context): Batch<IOType.D3>

        protected abstract fun train(
            input: Batch<IOType.D3>,
            context: Context,
            calcDelta: (Batch<IOType.D3>) -> Batch<IOType.D3>,
        ): Batch<IOType.D3>

        final override fun _expect(input: Batch<IOType>, context: Context): Batch<IOType> = expect(
            input = input as Batch<IOType.D3>,
            context = context,
        )

        final override fun _train(
            input: Batch<IOType>,
            context: Context,
            calcDelta: (Batch<IOType>) -> Batch<IOType>,
        ): Batch<IOType> = train(
            input = input as Batch<IOType.D3>,
            context = context,
            calcDelta = { input: Batch<IOType.D3> -> calcDelta(input) as Batch<IOType.D3> },
        )
    }
}
