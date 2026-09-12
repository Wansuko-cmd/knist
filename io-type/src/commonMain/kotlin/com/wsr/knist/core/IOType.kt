@file:Suppress("FunctionName")

package com.wsr.knist.core

import com.wsr.knist.base.data.DataBuffer
import kotlin.jvm.JvmName
import kotlinx.serialization.Serializable

@Serializable
sealed class IOType {
    abstract val value: DataBuffer
    abstract val shape: List<Int>
    abstract val size: Int

    @Serializable
    sealed class D0 : IOType() {
        override val shape = listOf(1)
        override val size = 1

        @PublishedApi
        context(scope: IOScope)
        internal fun toLocal() = Local(value).also { scope.register(it) }

        context(scope: IOScope)
        fun toGlobal() = Global(value).also { scope.remove(it) }

        @Serializable
        data class Local(override val value: DataBuffer) : D0()

        @Serializable
        data class Global(override val value: DataBuffer) : D0()
    }

    @Serializable
    sealed class D1 : IOType() {
        override val size get() = value.size
        override val shape get() = listOf(value.size)
        val i get() = value.size

        @PublishedApi
        context(scope: IOScope)
        internal fun toLocal() = Local(value).also { scope.register(it) }

        context(scope: IOScope)
        fun toGlobal() = Global(value).also { scope.remove(it) }

        @Serializable
        data class Local(override val value: DataBuffer) : D1()

        @Serializable
        data class Global(override val value: DataBuffer) : D1()
    }

    @Serializable
    sealed class D2 : IOType() {
        override val size get() = shape.reduce { acc, i -> acc * i }
        val i get() = shape[0]
        val j get() = shape[1]

        @PublishedApi
        context(scope: IOScope)
        internal fun toLocal() = Local(value, shape).also { scope.register(it) }

        context(scope: IOScope)
        fun toGlobal() = Global(value, shape).also { scope.remove(it) }

        @Serializable
        data class Local(override val value: DataBuffer, override val shape: List<Int>) : D2()

        @Serializable
        data class Global(override val value: DataBuffer, override val shape: List<Int>) : D2()
    }

    @Serializable
    sealed class D3 : IOType() {
        override val size get() = shape.reduce { acc, i -> acc * i }
        val i get() = shape[0]
        val j get() = shape[1]
        val k get() = shape[2]

        @PublishedApi
        context(scope: IOScope)
        internal fun toLocal() = Local(value, shape).also { scope.register(it) }

        context(scope: IOScope)
        fun toGlobal() = Global(value, shape).also { scope.remove(it) }

        @Serializable
        data class Local(override val value: DataBuffer, override val shape: List<Int>) : D3()

        @Serializable
        data class Global(override val value: DataBuffer, override val shape: List<Int>) : D3()
    }

    @Serializable
    sealed class D4 : IOType() {
        override val size get() = shape.reduce { acc, i -> acc * i }
        val i get() = shape[0]
        val j get() = shape[1]
        val k get() = shape[2]
        val l get() = shape[3]

        @PublishedApi
        context(scope: IOScope)
        internal fun toLocal() = Local(value, shape).also { scope.register(it) }

        context(scope: IOScope)
        fun toGlobal() = Global(value, shape).also { scope.remove(it) }

        @Serializable
        data class Local(override val value: DataBuffer, override val shape: List<Int>) : D4()

        @Serializable
        data class Global(override val value: DataBuffer, override val shape: List<Int>) : D4()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IOType) return false

        if (value != other.value) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }

    companion object {
        context(_: IOScope)
        fun D0(value: DataBuffer) = D0.Local(value)

        context(_: IOScope)
        fun D1(value: DataBuffer) = D1.Local(value)

        context(_: IOScope)
        fun D2(value: DataBuffer, shape: List<Int>) = D2.Local(value, shape)

        context(_: IOScope)
        fun D3(value: DataBuffer, shape: List<Int>) = D3.Local(value, shape)

        context(_: IOScope)
        fun D4(value: DataBuffer, shape: List<Int>) = D4.Local(value, shape)

        context(_: IOScope)
        fun d0(value: Float): D0.Local = d0Impl(value).toLocal()

        context(_: IOScope)
        fun d0(value: FloatArray): D0.Local = d0Impl(value).toLocal()

        context(_: IOScope)
        fun d1(value: FloatArray): D1.Local = d1Impl(value).toLocal()

        context(_: IOScope)
        inline fun d1(i: Int, init: (Int) -> Float = { 0f }): D1.Local = d1Impl(i, init).toLocal()

        context(_: IOScope)
        inline fun d1(shape: List<Int>, init: (Int) -> Float = { 0f }): D1.Local = d1Impl(shape, init).toLocal()

        context(_: IOScope)
        fun d1(value: List<Float>): D1.Local = d1Impl(value).toLocal()

        @JvmName("d1WithElements")
        context(_: IOScope)
        fun d1(vararg elements: Float): D1.Local = d1Impl(value = elements).toLocal()

        context(_: IOScope)
        fun d2(shape: List<Int>, value: FloatArray): D2.Local = d2Impl(shape, value).toLocal()

        context(_: IOScope)
        inline fun d2(i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): D2.Local = d2Impl(i, j, init).toLocal()

        context(_: IOScope)
        inline fun d2(shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }): D2.Local = d2Impl(shape, init).toLocal()

        context(_: IOScope)
        fun d2(shape: List<Int>, value: List<Float>): D2.Local = d2Impl(shape, value).toLocal()

        @JvmName("d2WithElements")
        context(_: IOScope)
        fun d2(vararg elements: D1): D2.Local = d2Impl(*elements).toLocal()

        context(_: IOScope)
        fun d3(shape: List<Int>, value: FloatArray): D3.Local = d3Impl(shape, value).toLocal()

        context(_: IOScope)
        inline fun d3(i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): D3.Local = d3Impl(i, j, k, init).toLocal()

        context(_: IOScope)
        inline fun d3(shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): D3.Local = d3Impl(shape, init).toLocal()

        context(_: IOScope)
        fun d3(shape: List<Int>, value: List<Float>): D3.Local = d3Impl(shape, value).toLocal()

        @JvmName("d3WithElements")
        context(_: IOScope)
        fun d3(vararg elements: D2): D3.Local = d3Impl(*elements).toLocal()

        context(_: IOScope)
        fun d4(shape: List<Int>, value: FloatArray): D4.Local = d4Impl(shape, value).toLocal()

        context(_: IOScope)
        inline fun d4(i: Int, j: Int, k: Int, l: Int, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): D4.Local = d4Impl(i, j, k, l, init).toLocal()

        context(_: IOScope)
        inline fun d4(shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): D4.Local = d4Impl(shape, init).toLocal()

        context(_: IOScope)
        fun d4(shape: List<Int>, value: List<Float>): D4.Local = d4Impl(shape, value).toLocal()

        @JvmName("d4WithElements")
        context(_: IOScope)
        fun d4(vararg elements: D3): D4.Local = d4Impl(*elements).toLocal()
    }
}

private fun IOScope.register(value: IOType) {
    bufferScope.register(value.value)
}

private fun IOScope.remove(value: IOType) {
    bufferScope.remove(value.value)
}

fun IOType.Companion.D0(value: DataBuffer) = IOType.D0.Global(value)

fun IOType.Companion.D1(value: DataBuffer) = IOType.D1.Global(value)

fun IOType.Companion.D2(value: DataBuffer, shape: List<Int>) = IOType.D2.Global(value, shape)

fun IOType.Companion.D3(value: DataBuffer, shape: List<Int>) = IOType.D3.Global(value, shape)

fun IOType.Companion.D4(value: DataBuffer, shape: List<Int>) = IOType.D4.Global(value, shape)

fun IOType.Companion.d0(value: Float): IOType.D0.Global = d0Impl(value)

fun IOType.Companion.d0(value: FloatArray): IOType.D0.Global = d0Impl(value)

fun IOType.Companion.d1(value: FloatArray): IOType.D1.Global = d1Impl(value)

inline fun IOType.Companion.d1(i: Int, init: (Int) -> Float = { 0f }): IOType.D1.Global = d1Impl(i, init)

inline fun IOType.Companion.d1(shape: List<Int>, init: (Int) -> Float = { 0f }): IOType.D1.Global = d1Impl(shape, init)

fun IOType.Companion.d1(value: List<Float>): IOType.D1.Global = d1Impl(value)

@JvmName("d1WithElements")
fun IOType.Companion.d1(vararg elements: Float): IOType.D1.Global = d1Impl(value = elements)

fun IOType.Companion.d2(shape: List<Int>, value: FloatArray): IOType.D2.Global = d2Impl(shape, value)

inline fun IOType.Companion.d2(
    i: Int,
    j: Int,
    init: (Int, Int) -> Float = { _, _ ->
        0f
    },
): IOType.D2.Global = d2Impl(i, j, init)

inline fun IOType.Companion.d2(
    shape: List<Int>,
    init: (Int, Int) -> Float = { _, _ ->
        0f
    },
): IOType.D2.Global = d2Impl(shape, init)

fun IOType.Companion.d2(shape: List<Int>, value: List<Float>): IOType.D2.Global = d2Impl(shape, value)

@JvmName("d2WithElements")
fun IOType.Companion.d2(vararg elements: IOType.D1): IOType.D2.Global = d2Impl(*elements)

fun IOType.Companion.d3(shape: List<Int>, value: FloatArray): IOType.D3.Global = d3Impl(shape, value)

inline fun IOType.Companion.d3(
    i: Int,
    j: Int,
    k: Int,
    init: (Int, Int, Int) -> Float = { _, _, _ ->
        0f
    },
): IOType.D3.Global = d3Impl(i, j, k, init)

inline fun IOType.Companion.d3(
    shape: List<Int>,
    init: (Int, Int, Int) -> Float = { _, _, _ ->
        0f
    },
): IOType.D3.Global = d3Impl(shape, init)

fun IOType.Companion.d3(shape: List<Int>, value: List<Float>): IOType.D3.Global = d3Impl(shape, value)

@JvmName("d3WithElements")
fun IOType.Companion.d3(vararg elements: IOType.D2): IOType.D3.Global = d3Impl(*elements)

fun IOType.Companion.d4(shape: List<Int>, value: FloatArray): IOType.D4.Global = d4Impl(shape, value)

inline fun IOType.Companion.d4(
    i: Int,
    j: Int,
    k: Int,
    l: Int,
    init: (Int, Int, Int, Int) -> Float = { _, _, _, _ ->
        0f
    },
): IOType.D4.Global = d4Impl(i, j, k, l, init)

inline fun IOType.Companion.d4(
    shape: List<Int>,
    init: (Int, Int, Int, Int) -> Float = { _, _, _, _ ->
        0f
    },
): IOType.D4.Global = d4Impl(shape, init)

fun IOType.Companion.d4(shape: List<Int>, value: List<Float>): IOType.D4.Global = d4Impl(shape, value)

@JvmName("d4WithElements")
fun IOType.Companion.d4(vararg elements: IOType.D3): IOType.D4.Global = d4Impl(*elements)
