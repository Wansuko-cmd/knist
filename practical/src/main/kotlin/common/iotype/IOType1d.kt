@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

data class IOType1d(val value: Array<DoubleArray>) : IOType {

    override inline fun asIOType0d(): IOType0d = IOType0d(value.fold(doubleArrayOf()) { acc, a -> acc + a })
    override inline fun asIOType1d(): IOType1d = this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IOType1d

        if (!value.contentDeepEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return value.contentDeepHashCode()
    }
}
