@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

data class IOType2d(val value: Array<Array<DoubleArray>>) : IOType {

    override inline fun asIOType0d(): IOType0d = throw Exception()
    override inline fun asIOType1d(): IOType1d = throw Exception()
    override fun asIOType2d(): IOType2d {
        TODO("Not yet implemented")
    }
}
