package com.wsr.layer.reshape

import com.wsr.IOType
import com.wsr.layer.Context
import com.wsr.layer.Layer
import kotlinx.serialization.Serializable

@Suppress("UNCHECKED_CAST")
sealed interface Reshape : Layer {
    @Serializable
    abstract class D1ToD2 : Reshape {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: List<IOType.D1>, context: Context): List<IOType.D2>

        protected abstract fun train(
            input: List<IOType.D1>,
            context: Context,
            calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
        ): List<IOType.D1>

        final override fun _expect(input: List<IOType>, context: Context): List<IOType> = expect(
            input = input as List<IOType.D1>,
            context = context,
        )

        final override fun _train(
            input: List<IOType>,
            context: Context,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> = train(
            input = input as List<IOType.D1>,
            context = context,
            calcDelta = { input: List<IOType.D2> -> calcDelta(input) as List<IOType.D2> },
        )
    }

    @Serializable
    abstract class D2ToD1 : Reshape {
        abstract val outputSize: Int

        protected abstract fun expect(input: List<IOType.D2>, context: Context): List<IOType.D1>

        protected abstract fun train(
            input: List<IOType.D2>,
            context: Context,
            calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
        ): List<IOType.D2>

        final override fun _expect(input: List<IOType>, context: Context): List<IOType> = expect(
            input = input as List<IOType.D2>,
            context = context,
        )

        final override fun _train(
            input: List<IOType>,
            context: Context,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> = train(
            input = input as List<IOType.D2>,
            context = context,
            calcDelta = { input: List<IOType.D1> -> calcDelta(input) as List<IOType.D1> },
        )
    }

    @Serializable
    abstract class D3ToD2 : Reshape {
        abstract val outputX: Int
        abstract val outputY: Int

        protected abstract fun expect(input: List<IOType.D3>, context: Context): List<IOType.D2>

        protected abstract fun train(
            input: List<IOType.D3>,
            context: Context,
            calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
        ): List<IOType.D3>

        final override fun _expect(input: List<IOType>, context: Context): List<IOType> =
            expect(input = input as List<IOType.D3>, context = context)

        final override fun _train(
            input: List<IOType>,
            context: Context,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> = train(
            input = input as List<IOType.D3>,
            context = context,
            calcDelta = { input: List<IOType.D2> -> calcDelta(input) as List<IOType.D2> },
        )
    }

    @Serializable
    abstract class D3ToD1 : Reshape {
        abstract val outputSize: Int

        protected abstract fun expect(input: List<IOType.D3>, context: Context): List<IOType.D1>

        protected abstract fun train(
            input: List<IOType.D3>,
            context: Context,
            calcDelta: (List<IOType.D1>) -> List<IOType.D1>,
        ): List<IOType.D3>

        final override fun _expect(input: List<IOType>, context: Context): List<IOType> =
            expect(input = input as List<IOType.D3>, context = context)

        final override fun _train(
            input: List<IOType>,
            context: Context,
            calcDelta: (List<IOType>) -> List<IOType>,
        ): List<IOType> = train(
            input = input as List<IOType.D3>,
            context = context,
            calcDelta = { input: List<IOType.D1> -> calcDelta(input) as List<IOType.D1> },
        )
    }
}
