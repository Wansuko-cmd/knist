@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

import exception.DomainException

class IOType0d(value: DoubleArray) : IOType {

    val inner: DoubleArray = value
    val indices = value.indices
    val size: Int = value.size

    override inline fun asIOType0d(): IOType0d = this
    override inline fun asIOType1d(): IOType1d = throw DomainException.CannotCastDimensionException()
    override inline fun asIOType2d(): IOType2d = throw DomainException.CannotCastDimensionException()

    inline operator fun get(index: Int) = inner[index]
    inline operator fun set(index: Int, value: Double) {
        inner[index] = value
    }
}
