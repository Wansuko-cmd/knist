package com.wsr.knist.base

import com.wsr.knist.base.data.DataBuffer
import com.wsr.knist.base.data.Default
import com.wsr.knist.base.data.indices

internal inline fun DataBuffer.map(block: (Float) -> Float): DataBuffer {
    val result = Default(size)
    for (i in result.indices) result[i] = block(this[i])
    return result
}

internal inline fun DataBuffer.zipWith(other: DataBuffer, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    for (i in result.indices) result[i] = block(this[i], other[i])
    return result
}

internal inline fun DataBuffer.zipWith(other: DataBuffer, yi: Int, yj: Int, axis: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(other.size)
    when (axis) {
        0 -> {
            for (i in 0 until yi) {
                val thisValue = this[i]
                val oi = i * yj
                for (j in 0 until yj) {
                    val index = oi + j
                    result[index] = block(thisValue, other[index])
                }
            }
        }

        1 -> {
            for (i in 0 until yi) {
                val oi = i * yj
                for (j in 0 until yj) {
                    val index = oi + j
                    result[index] = block(this[j], other[index])
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(other: DataBuffer, yi: Int, yj: Int, yk: Int, axis: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(other.size)
    when (axis) {
        0 -> {
            for (i in 0 until yi) {
                val thisValue = this[i]
                for (j in 0 until yj) {
                    val oi = (i * yj + j) * yk
                    for (k in 0 until yk) {
                        val index = oi + k
                        result[index] = block(thisValue, other[index])
                    }
                }
            }
        }

        1 -> {
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    val thisValue = this[j]
                    val oi = (i * yj + j) * yk
                    for (k in 0 until yk) {
                        val index = oi + k
                        result[index] = block(thisValue, other[index])
                    }
                }
            }
        }

        2 -> {
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    val oi = (i * yj + j) * yk
                    for (k in 0 until yk) {
                        val index = oi + k
                        result[index] = block(this[k], other[index])
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, other: DataBuffer, axis: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    when (axis) {
        0 -> {
            for (i in 0 until xi) {
                val ti = i * xj
                val otherValue = other[i]
                for (j in 0 until xj) {
                    val index = ti + j
                    result[index] = block(this[index], otherValue)
                }
            }
        }

        1 -> {
            for (i in 0 until xi) {
                val ti = i * xj
                for (j in 0 until xj) {
                    val index = ti + j
                    result[index] = block(this[index], other[j])
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, other: DataBuffer, yi: Int, yj: Int, yk: Int, axis1: Int, axis2: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(other.size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> {
                for (i in 0 until yi) {
                    for (j in 0 until yj) {
                        val thisValue = this[i * yj + j]
                        val oi = (i * yj + j) * yk
                        for (k in 0 until yk) {
                            val otherIndex = oi + k
                            result[otherIndex] = block(thisValue, other[otherIndex])
                        }
                    }
                }
            }

            2 -> {
                for (i in 0 until yi) {
                    for (j in 0 until yj) {
                        val ti = i * yk
                        val oi = (i * yj + j) * yk
                        for (k in 0 until yk) {
                            val thisIndex = ti + k
                            val otherIndex = oi + k
                            result[otherIndex] = block(this[thisIndex], other[otherIndex])
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> {
                for (i in 0 until yi) {
                    for (j in 0 until yj) {
                        val ti = j * yk
                        val oi = (i * yj + j) * yk
                        for (k in 0 until yk) {
                            val thisIndex = ti + k
                            val otherIndex = oi + k
                            result[otherIndex] = block(this[thisIndex], other[otherIndex])
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, xk: Int, other: DataBuffer, axis: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    when (axis) {
        0 -> {
            for (i in 0 until xi) {
                val otherValue = other[i]
                for (j in 0 until xj) {
                    val ti = (i * xj + j) * xk
                    for (k in 0 until xk) {
                        val index = ti + k
                        result[index] = block(this[index], otherValue)
                    }
                }
            }
        }

        1 -> {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val ti = (i * xj + j) * xk
                    val otherValue = other[j]
                    for (k in 0 until xk) {
                        val index = ti + k
                        result[index] = block(this[index], otherValue)
                    }
                }
            }
        }

        2 -> {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val ti = (i * xj + j) * xk
                    for (k in 0 until xk) {
                        val index = ti + k
                        result[index] = block(this[index], other[k])
                    }
                }
            }
        }
    }

    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, xk: Int, other: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val ti = (i * xj + j) * xk
                        val otherValue = other[i * xj + j]
                        for (k in 0 until xk) {
                            val thisIndex = ti + k
                            result[thisIndex] = block(this[thisIndex], otherValue)
                        }
                    }
                }
            }

            2 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val ti = (i * xj + j) * xk
                        for (k in 0 until xk) {
                            val thisIndex = ti + k
                            val otherIndex = i * xk + k
                            result[thisIndex] = block(this[thisIndex], other[otherIndex])
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val ti = (i * xj + j) * xk
                        for (k in 0 until xk) {
                            val thisIndex = ti + k
                            val otherIndex = j * xk + k
                            result[thisIndex] = block(this[thisIndex], other[otherIndex])
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(
    xi: Int,
    xj: Int,
    xk: Int,
    other: DataBuffer,
    yi: Int,
    yj: Int,
    yk: Int,
    yl: Int,
    axis1: Int,
    axis2: Int,
    axis3: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = Default(other.size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> when (axis3) {
                2 -> {
                    for (i in 0 until yi) {
                        for (j in 0 until yj) {
                            for (k in 0 until yk) {
                                val thisValue = this[(i * yj + j) * yk + k]
                                val oi = ((i * yj + j) * yk + k) * yl
                                for (l in 0 until yl) {
                                    val otherIndex = oi + l
                                    result[otherIndex] = block(thisValue, other[otherIndex])
                                }
                            }
                        }
                    }
                }

                3 -> {
                    for (i in 0 until yi) {
                        for (j in 0 until yj) {
                            for (k in 0 until yk) {
                                val oi = ((i * yj + j) * yk + k) * yl
                                for (l in 0 until yl) {
                                    val thisIndex = (i * yj + j) * yl + l
                                    val otherIndex = oi + l
                                    result[otherIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }

            2 -> when (axis3) {
                3 -> {
                    for (i in 0 until yi) {
                        for (j in 0 until yj) {
                            for (k in 0 until yk) {
                                val oi = ((i * yj + j) * yk + k) * yl
                                for (l in 0 until yl) {
                                    val thisIndex = (i * yk + k) * yl + l
                                    val otherIndex = oi + l
                                    result[otherIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> when (axis3) {
                3 -> {
                    for (i in 0 until yi) {
                        for (j in 0 until yj) {
                            for (k in 0 until yk) {
                                val oi = ((i * yj + j) * yk + k) * yl
                                for (l in 0 until yl) {
                                    val thisIndex = (j * yk + k) * yl + l
                                    val otherIndex = oi + l
                                    result[otherIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, xk: Int, xl: Int, other: DataBuffer, axis: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    when (axis) {
        0 -> {
            for (i in 0 until xi) {
                val otherValue = other[i]
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val ti = ((i * xj + j) * xk + k) * xl
                        for (l in 0 until xl) {
                            val thisIndex = ti + l
                            result[thisIndex] = block(this[thisIndex], otherValue)
                        }
                    }
                }
            }
        }

        1 -> {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val otherValue = other[j]
                    for (k in 0 until xk) {
                        val ti = ((i * xj + j) * xk + k) * xl
                        for (l in 0 until xl) {
                            val thisIndex = ti + l
                            result[thisIndex] = block(this[thisIndex], otherValue)
                        }
                    }
                }
            }
        }

        2 -> {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val ti = ((i * xj + j) * xk + k) * xl
                        val otherValue = other[k]
                        for (l in 0 until xl) {
                            val thisIndex = ti + l
                            result[thisIndex] = block(this[thisIndex], otherValue)
                        }
                    }
                }
            }
        }

        3 -> {
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val ti = ((i * xj + j) * xk + k) * xl
                        for (l in 0 until xl) {
                            val thisIndex = ti + l
                            result[thisIndex] = block(this[thisIndex], other[l])
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(xi: Int, xj: Int, xk: Int, xl: Int, other: DataBuffer, yi: Int, yj: Int, axis1: Int, axis2: Int, block: (Float, Float) -> Float): DataBuffer {
    val result = Default(size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val otherValue = other[i * xj + j]
                        for (k in 0 until xk) {
                            val ti = ((i * xj + j) * xk + k) * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                result[thisIndex] = block(this[thisIndex], otherValue)
                            }
                        }
                    }
                }
            }

            2 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        for (k in 0 until xk) {
                            val otherValue = other[i * xk + k]
                            val ti = ((i * xj + j) * xk + k) * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                result[thisIndex] = block(this[thisIndex], otherValue)
                            }
                        }
                    }
                }
            }

            3 -> {
                for (i in 0 until xi) {
                    val oi = i * xl
                    for (j in 0 until xj) {
                        for (k in 0 until xk) {
                            val ti = ((i * xj + j) * xk + k) * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                val otherIndex = oi + l
                                result[thisIndex] = block(this[thisIndex], other[otherIndex])
                            }
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        for (k in 0 until xk) {
                            val otherValue = other[j * xk + k]
                            val ti = ((i * xj + j) * xk + k) * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                result[thisIndex] = block(this[thisIndex], otherValue)
                            }
                        }
                    }
                }
            }

            3 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val oi = j * xl
                        for (k in 0 until xk) {
                            val ti = ((i * xj + j) * xk + k) * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                val otherIndex = oi + l
                                result[thisIndex] = block(this[thisIndex], other[otherIndex])
                            }
                        }
                    }
                }
            }
        }

        2 -> when (axis2) {
            3 -> {
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        for (k in 0 until xk) {
                            val ti = ((i * xj + j) * xk + k) * xl
                            val oi = k * xl
                            for (l in 0 until xl) {
                                val thisIndex = ti + l
                                val otherIndex = oi + l
                                result[thisIndex] = block(this[thisIndex], other[otherIndex])
                            }
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(
    xi: Int,
    xj: Int,
    xk: Int,
    xl: Int,
    other: DataBuffer,
    yi: Int,
    yj: Int,
    yk: Int,
    axis1: Int,
    axis2: Int,
    axis3: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = Default(size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> when (axis3) {
                2 -> {
                    for (i in 0 until xi) {
                        for (j in 0 until xj) {
                            for (k in 0 until xk) {
                                val ti = ((i * xj + j) * xk + k) * xl
                                val otherValue = other[(i * xj + j) * xk + k]
                                for (l in 0 until xl) {
                                    val thisIndex = ti + l
                                    result[thisIndex] = block(this[thisIndex], otherValue)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    for (i in 0 until xi) {
                        for (j in 0 until xj) {
                            for (k in 0 until xk) {
                                val ti = ((i * xj + j) * xk + k) * xl
                                val oi = (i * xj + j) * xl
                                for (l in 0 until xl) {
                                    val thisIndex = ti + l
                                    val otherIndex = oi + l
                                    result[thisIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }

            2 -> when (axis3) {
                3 -> {
                    for (i in 0 until xi) {
                        for (j in 0 until xj) {
                            for (k in 0 until xk) {
                                val ti = ((i * xj + j) * xk + k) * xl
                                val oi = (i * xk + k) * xl
                                for (l in 0 until xl) {
                                    val thisIndex = ti + l
                                    val otherIndex = oi + l
                                    result[thisIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> when (axis3) {
                3 -> {
                    for (i in 0 until xi) {
                        for (j in 0 until xj) {
                            for (k in 0 until xk) {
                                val ti = ((i * xj + j) * xk + k) * xl
                                val oi = (j * xk + k) * xl
                                for (l in 0 until xl) {
                                    val thisIndex = ti + l
                                    val otherIndex = oi + l
                                    result[thisIndex] = block(this[thisIndex], other[otherIndex])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.reduce(operation: (Float, Float) -> Float): DataBuffer {
    var acc = this[0]
    for (i in 1 until size) {
        acc = operation(acc, this[i])
    }
    return Default(floatArrayOf(acc))
}

internal inline fun DataBuffer.reduce(xi: Int, xj: Int, axis: Int, operation: (Float, Float) -> Float): DataBuffer = when (axis) {
    0 -> {
        val result = Default(size = xj)
        for (j in 0 until xj) {
            var acc = this[j]
            for (i in 1 until xi) {
                acc = operation(acc, this[i * xj + j])
            }
            result[j] = acc
        }
        result
    }

    1 -> {
        val result = Default(size = xi)
        for (i in 0 until xi) {
            var acc = this[i * xj]
            for (j in 1 until xj) {
                acc = operation(acc, this[i * xj + j])
            }
            result[i] = acc
        }
        result
    }

    else -> throw IllegalArgumentException()
}

internal inline fun DataBuffer.reduce(xi: Int, xj: Int, xk: Int, axis: Int, operation: (Float, Float) -> Float): DataBuffer = when (axis) {
    0 -> {
        val result = Default(xj * xk)
        for (j in 0 until xj) {
            for (k in 0 until xk) {
                var acc = this[j * xk + k]
                for (i in 1 until xi) {
                    acc = operation(acc, this[(i * xj + j) * xk + k])
                }
                result[j * xk + k] = acc
            }
        }
        result
    }

    1 -> {
        val result = Default(xi * xk)
        for (i in 0 until xi) {
            for (k in 0 until xk) {
                var acc = this[i * xj * xk + k]
                for (j in 1 until xj) {
                    acc = operation(acc, this[(i * xj + j) * xk + k])
                }
                result[i * xk + k] = acc
            }
        }
        result
    }

    2 -> {
        val result = Default(xi * xj)
        for (i in 0 until xi) {
            for (j in 0 until xj) {
                var acc = this[(i * xj + j) * xk]
                for (k in 1 until xk) {
                    acc = operation(acc, this[(i * xj + j) * xk + k])
                }
                result[i * xj + j] = acc
            }
        }
        result
    }

    else -> throw IllegalArgumentException()
}

internal inline fun DataBuffer.reduceIndex(xi: Int, xj: Int, axis: Int, operation: (DataBuffer) -> Float): DataBuffer {
    val x = toFloatArray()
    return when (axis) {
        0 -> {
            val result = Default(xj)
            for (j in 0 until xj) {
                val target = Default(FloatArray(xi) { x[it * xj + j] })
                result[j] = operation(target)
            }
            result
        }

        1 -> {
            val result = Default(xi)
            for (i in 0 until xi) {
                val target = Default(FloatArray(xj) { x[i * xj + it] })
                result[i] = operation(target)
            }
            result
        }

        else -> throw IllegalArgumentException()
    }
}

internal inline fun DataBuffer.reduceIndex(xi: Int, xj: Int, xk: Int, axis: Int, operation: (DataBuffer) -> Float): DataBuffer {
    val x = toFloatArray()
    return when (axis) {
        0 -> {
            val result = Default(xj * xk)
            for (j in 0 until xj) {
                for (k in 0 until xk) {
                    val target = Default(FloatArray(xi) { x[(it * xj + j) * xk + k] })
                    result[j * xk + k] = operation(target)
                }
            }
            result
        }

        1 -> {
            val result = Default(xi * xk)
            for (i in 0 until xi) {
                for (k in 0 until xk) {
                    val target = Default(FloatArray(xj) { x[(i * xj + it) * xk + k] })
                    result[i * xk + k] = operation(target)
                }
            }
            result
        }

        2 -> {
            val result = Default(xi * xj)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val target = Default(FloatArray(xk) { x[(i * xj + j) * xk + it] })
                    result[i * xj + j] = operation(target)
                }
            }
            result
        }

        else -> throw IllegalArgumentException()
    }
}
