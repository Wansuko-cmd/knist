@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

data class IOType2d(val value: Array<Array<DoubleArray>>) : IOType {

    override inline fun asIOType0d(): IOType0d = IOType0d(value.flatten().fold(doubleArrayOf()) { acc, a -> acc + a })
    override inline fun asIOType1d(): IOType1d = IOType1d(value.flatten().toTypedArray())
    override inline fun asIOType2d(): IOType2d = this
}
