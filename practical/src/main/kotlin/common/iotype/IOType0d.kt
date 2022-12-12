@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

data class IOType0d(val value: DoubleArray) : IOType {
    override inline fun asIOType0d(): IOType0d = this
    override inline fun asIOType1d(): IOType1d = throw Exception()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IOType0d

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}
