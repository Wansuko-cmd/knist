@file:Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")

package common.iotype

interface IOType {
    fun asIOType0d(): IOType0d
    fun asIOType1d(): IOType1d
    fun asIOType2d(): IOType2d
}
