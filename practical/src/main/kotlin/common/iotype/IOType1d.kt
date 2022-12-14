@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

import exception.DomainException

class IOType1d(value: Array<DoubleArray>) : IOType {

    val inner = value.fold(doubleArrayOf()) { acc, element -> acc + element }
    val indexSize = value.size
    val timeSize = value.first().size
    val indices = 0 until indexSize

    inline operator fun get(index: Int): IOType0d =
        IOType0d(inner.sliceArray(timeSize * index until timeSize * index + timeSize))

    inline operator fun get(index: Int, time: Int) = inner[timeSize * index + time]
    inline operator fun set(index: Int, time: Int, value: Double) {
        inner[indexSize * index + time] = value
    }

    override inline fun asIOType0d(): IOType0d = IOType0d(inner)
    override inline fun asIOType1d(): IOType1d = this
    override inline fun asIOType2d(): IOType2d = throw DomainException.CannotCastDimensionException()
}
