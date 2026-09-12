package com.wsr.knist.batch

import com.wsr.knist.Backend
import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.indices
import com.wsr.knist.core.IOScope
import com.wsr.knist.core.IOType
import kotlin.jvm.JvmName

class Batch<out T : IOType>(val value: DataBuffer, val size: Int, val shape: List<Int>) {
    companion object {
        fun <T : IOType> of(vararg elements: T): Batch<T> {
            val size = elements.size
            val shape = elements.first().shape
            val step = shape.reduce { acc, i -> acc * i }
            val batchValue = DataBuffer.create(size * step)
            elements.forEachIndexed { index, item ->
                val start = index * step
                Backend.copyInto(item.value, batchValue, start until start + item.value.size)
            }
            return Batch(
                value = batchValue,
                size = size,
                shape = shape,
            )
        }

        context(_: IOScope)
        inline fun d0(size: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D0.Local> = d0Impl(size, init).toLocal()

        context(_: IOScope)
        inline fun d1(size: Int, i: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D1.Local> = d1Impl(size, i, init).toLocal()

        context(_: IOScope)
        inline fun d1(size: Int, shape: List<Int>, init: (Int) -> Float = { 0f }): Batch<IOType.D1.Local> = d1Impl(size, shape, init).toLocal()

        context(_: IOScope)
        fun d1(size: Int, i: Int, value: FloatArray): Batch<IOType.D1.Local> = d1Impl(size, i, value).toLocal()

        context(_: IOScope)
        inline fun d2(size: Int, i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Local> = d2Impl(size, i, j, init).toLocal()

        context(_: IOScope)
        inline fun d2(size: Int, shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Local> = d2Impl(size, shape, init).toLocal()

        context(_: IOScope)
        fun d2(size: Int, i: Int, j: Int, value: FloatArray): Batch<IOType.D2.Local> = d2Impl(size, i, j, value).toLocal()

        context(_: IOScope)
        inline fun d3(size: Int, i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Local> = d3Impl(size, i, j, k, init).toLocal()

        context(_: IOScope)
        inline fun d3(size: Int, shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Local> = d3Impl(size, shape, init).toLocal()

        context(_: IOScope)
        inline fun d4(size: Int, i: Int, j: Int, k: Int, l: Int, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Local> = d4Impl(size, i, j, k, l, init).toLocal()

        context(_: IOScope)
        inline fun d4(size: Int, shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Local> = d4Impl(size, shape, init).toLocal()
    }

    val step = shape.reduce { acc, i -> acc * i }
    val indices = 0 until size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Batch<*>) return false

        if (size != other.size) return false
        if (value != other.value) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + value.hashCode()
        result = 31 * result + shape.hashCode()
        return result
    }

    override fun toString(): String = "Batch(shape=$shape, size=$size, value=$value)"
}

inline fun <T : IOType> Batch(size: Int, init: (index: Int) -> T): Batch<T> {
    val first = init(0)
    val value = DataBuffer.create(size * first.value.size)
    Backend.copyInto(first.value, value, first.value.indices)
    for (i in 1 until size) {
        val src = init(i).value
        val start = i * first.value.size
        Backend.copyInto(src, value, start until start + src.size)
    }
    return Batch(
        value = value,
        size = size,
        shape = first.shape,
    )
}

@PublishedApi
@JvmName("batchD0ToLocal")
context(scope: IOScope)
internal fun Batch<IOType.D0>.toLocal(): Batch<IOType.D0.Local> = Batch<IOType.D0.Local>(value, size, shape).also { scope.bufferScope.register(value) }

@PublishedApi
@JvmName("batchD1ToLocal")
context(scope: IOScope)
internal fun Batch<IOType.D1>.toLocal(): Batch<IOType.D1.Local> = Batch<IOType.D1.Local>(value, size, shape).also { scope.bufferScope.register(value) }

@PublishedApi
@JvmName("batchD2ToLocal")
context(scope: IOScope)
internal fun Batch<IOType.D2>.toLocal(): Batch<IOType.D2.Local> = Batch<IOType.D2.Local>(value, size, shape).also { scope.bufferScope.register(value) }

@PublishedApi
@JvmName("batchD3ToLocal")
context(scope: IOScope)
internal fun Batch<IOType.D3>.toLocal(): Batch<IOType.D3.Local> = Batch<IOType.D3.Local>(value, size, shape).also { scope.bufferScope.register(value) }

@PublishedApi
@JvmName("batchD4ToLocal")
context(scope: IOScope)
internal fun Batch<IOType.D4>.toLocal(): Batch<IOType.D4.Local> = Batch<IOType.D4.Local>(value, size, shape).also { scope.bufferScope.register(value) }

@JvmName("batchD0ToGlobal")
context(scope: IOScope)
fun Batch<IOType.D0>.toGlobal(): Batch<IOType.D0.Global> = Batch<IOType.D0.Global>(value, size, shape).also { scope.bufferScope.remove(value) }

@JvmName("batchD1ToGlobal")
context(scope: IOScope)
fun Batch<IOType.D1>.toGlobal(): Batch<IOType.D1.Global> = Batch<IOType.D1.Global>(value, size, shape).also { scope.bufferScope.remove(value) }

@JvmName("batchD2ToGlobal")
context(scope: IOScope)
fun Batch<IOType.D2>.toGlobal(): Batch<IOType.D2.Global> = Batch<IOType.D2.Global>(value, size, shape).also { scope.bufferScope.remove(value) }

@JvmName("batchD3ToGlobal")
context(scope: IOScope)
fun Batch<IOType.D3>.toGlobal(): Batch<IOType.D3.Global> = Batch<IOType.D3.Global>(value, size, shape).also { scope.bufferScope.remove(value) }

@JvmName("batchD4ToGlobal")
context(scope: IOScope)
fun Batch<IOType.D4>.toGlobal(): Batch<IOType.D4.Global> = Batch<IOType.D4.Global>(value, size, shape).also { scope.bufferScope.remove(value) }

inline fun Batch.Companion.d0(size: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D0.Global> = d0Impl(size, init)

inline fun Batch.Companion.d1(size: Int, i: Int, init: (Int) -> Float = { 0f }): Batch<IOType.D1.Global> = d1Impl(size, i, init)

inline fun Batch.Companion.d1(
    size: Int,
    shape: List<Int>,
    init: (Int) -> Float = {
        0f
    },
): Batch<IOType.D1.Global> = d1Impl(size, shape, init)

fun Batch.Companion.d1(size: Int, i: Int, value: FloatArray): Batch<IOType.D1.Global> = d1Impl(size, i, value)

inline fun Batch.Companion.d2(size: Int, i: Int, j: Int, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Global> = d2Impl(size, i, j, init)

inline fun Batch.Companion.d2(size: Int, shape: List<Int>, init: (Int, Int) -> Float = { _, _ -> 0f }): Batch<IOType.D2.Global> = d2Impl(size, shape, init)

fun Batch.Companion.d2(size: Int, i: Int, j: Int, value: FloatArray): Batch<IOType.D2.Global> = d2Impl(size, i, j, value)

inline fun Batch.Companion.d3(size: Int, i: Int, j: Int, k: Int, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Global> = d3Impl(size, i, j, k, init)

inline fun Batch.Companion.d3(size: Int, shape: List<Int>, init: (Int, Int, Int) -> Float = { _, _, _ -> 0f }): Batch<IOType.D3.Global> = d3Impl(size, shape, init)

inline fun Batch.Companion.d4(size: Int, i: Int, j: Int, k: Int, l: Int, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Global> = d4Impl(size, i, j, k, l, init)

inline fun Batch.Companion.d4(size: Int, shape: List<Int>, init: (Int, Int, Int, Int) -> Float = { _, _, _, _ -> 0f }): Batch<IOType.D4.Global> = d4Impl(size, shape, init)

fun Batch.Companion.d0(size: Int, value: DataBuffer): Batch<IOType.D0.Global> = d0Impl(size, value)

fun Batch.Companion.d1(size: Int, i: Int, value: DataBuffer): Batch<IOType.D1.Global> = d1Impl(size, i, value)

fun Batch.Companion.d1(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D1.Global> = d1Impl(size, shape, value)

fun Batch.Companion.d2(size: Int, i: Int, j: Int, value: DataBuffer): Batch<IOType.D2.Global> = d2Impl(size, i, j, value)

fun Batch.Companion.d2(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D2.Global> = d2Impl(size, shape, value)

fun Batch.Companion.d3(size: Int, i: Int, j: Int, k: Int, value: DataBuffer): Batch<IOType.D3.Global> = d3Impl(size, i, j, k, value)

fun Batch.Companion.d3(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D3.Global> = d3Impl(size, shape, value)

fun Batch.Companion.d4(size: Int, i: Int, j: Int, k: Int, l: Int, value: DataBuffer): Batch<IOType.D4.Global> = d4Impl(size, i, j, k, l, value)

fun Batch.Companion.d4(size: Int, shape: List<Int>, value: DataBuffer): Batch<IOType.D4.Global> = d4Impl(size, shape, value)
