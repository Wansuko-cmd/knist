package com.wsr

class Batch<out T : IOType>(
    val value: FloatArray,
    val size: Int,
    val shape: List<Int>,
) {
    internal val step = shape.reduce { acc, i -> acc * i }

    fun copy(
        value: FloatArray = this.value.copyOf(),
        size: Int = this.size,
        shape: List<Int> = this.shape,
    ) = Batch<T>(value = value, size = size, shape = shape)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Batch<*>

        if (size != other.size) return false
        if (!value.contentEquals(other.value)) return false
        if (shape != other.shape) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + value.contentHashCode()
        result = 31 * result + shape.hashCode()
        return result
    }
}

fun <T : IOType> batchOf(vararg elements: T): Batch<T> {
    val batchSize = elements.size
    val shape = elements.first().shape
    val step = shape.reduce { acc, i -> acc * i }
    val batchValue = FloatArray(batchSize * step)
    elements.forEachIndexed { index, item ->
        item.value.copyInto(batchValue, index * step)
    }
    return Batch(
        value = batchValue,
        size = batchSize,
        shape = shape,
    )
}

@JvmName("batchD1sGet")
operator fun Batch<IOType.D1>.get(i: Int): IOType.D1 {
    val index = i * step
    return IOType.d1(value.sliceArray(index until index + step))
}

@JvmName("batchD2sGet")
operator fun Batch<IOType.D2>.get(i: Int): IOType.D2 {
    val index = i * step
    return IOType.d2(shape, value.sliceArray(index until index + step))
}

@JvmName("batchD3sGet")
operator fun Batch<IOType.D3>.get(i: Int): IOType.D3 {
    val index = i * step
    return IOType.d3(shape, value.sliceArray(index until index + step))
}

operator fun Batch<IOType.D1>.set(i: Int, element: IOType.D1) {
    element.value.copyInto(value, i * step)
}

operator fun Batch<IOType.D2>.set(i: Int, element: IOType.D2) {
    element.value.copyInto(value, i * step)
}

operator fun Batch<IOType.D3>.set(i: Int, element: IOType.D3) {
    element.value.copyInto(value, i * step)
}
