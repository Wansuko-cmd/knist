@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

import exception.DomainException

class IOType1d(
    val inner: MutableList<Double>,
    val indexSize: Int,
    val timeSize: Int,
) : IOType {

    val indices = 0 until indexSize

    inline operator fun get(index: Int): IOType0d =
        IOType0d(inner.subList(timeSize * index, timeSize * index + timeSize))

    inline operator fun get(index: Int, time: Int) = inner[timeSize * index + time]
    inline operator fun set(index: Int, time: Int, value: Double) {
        inner[timeSize * index + time] = value
    }

    override inline fun asIOType0d(): IOType0d = IOType0d(inner)
    override inline fun asIOType1d(): IOType1d = this
    override inline fun asIOType2d(): IOType2d = throw DomainException.CannotCastDimensionException()

    companion object {
        fun create(value: List<List<Double>>) = IOType1d(
            inner = value.flatten().toMutableList(),
            indexSize = value.size,
            timeSize = value.first().size,
        )
    }
}
