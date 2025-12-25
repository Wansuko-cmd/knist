package com.wsr.base

internal inline fun DataBuffer.map(block: (Float) -> Float): DataBuffer {
    val result = DataBuffer.create(size)
    for (i in result.indices) result[i] = block(this[i])
    return result
}

internal inline fun DataBuffer.zipWith(other: DataBuffer, block: (Float, Float) -> Float): DataBuffer {
    val result = DataBuffer.create(size)
    for (i in result.indices) result[i] = block(this[i], other[i])
    return result
}

internal inline fun DataBuffer.zipWith(
    other: DataBuffer,
    yi: Int,
    yj: Int,
    axis: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(other.size)
    when (axis) {
        0 -> {
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    val index = i * yj + j
                    result[index] = block(this[i], other[index])
                }
            }
        }

        1 -> {
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    val index = i * yj + j
                    result[index] = block(this[j], other[index])
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(
    other: DataBuffer,
    yi: Int,
    yj: Int,
    yk: Int,
    axis: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(other.size)
    when (axis) {
        0 -> {
            check(size == yi)
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    for (k in 0 until yk) {
                        val index = ((i * yj + j) * yk) + k
                        result[index] = block(this[i], other[index])
                    }
                }
            }
        }

        1 -> {
            check(size == yj)
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    for (k in 0 until yk) {
                        val index = ((i * yj + j) * yk) + k
                        result[index] = block(this[j], other[index])
                    }
                }
            }
        }

        2 -> {
            check(size == yk)
            for (i in 0 until yi) {
                for (j in 0 until yj) {
                    for (k in 0 until yk) {
                        val index = ((i * yj + j) * yk) + k
                        result[index] = block(this[k], other[index])
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
    other: DataBuffer,
    axis: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(size)
    when (axis) {
        0 -> {
            check(xi == other.size)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val index = i * xj + j
                    result[index] = block(this[index], other[i])
                }
            }
        }

        1 -> {
            check(xj == other.size)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    val index = i * xj + j
                    result[index] = block(this[index], other[j])
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.zipWith(
    xi: Int,
    xj: Int,
    other: DataBuffer,
    yi: Int,
    yj: Int,
    yk: Int,
    axis1: Int,
    axis2: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(other.size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> {
                check(xi == yi && xj == yj)
                for (i in 0 until yi) {
                    for (j in 0 until yj) {
                        val indexIJ = i * yj + j
                        for (k in 0 until yk) {
                            val index = (i * yj + j) * yk + k
                            result[index] = block(this[indexIJ], this[index])
                        }
                    }
                }
            }

            2 -> {
                check(xi == yi && xj == yk)
                for (i in 0 until yi) {
                    for (k in 0 until yk) {
                        val indexIK = i * yk + k
                        for (j in 0 until yj) {
                            val index = (i * yj + j) * yk + k
                            result[index] = block(this[indexIK], this[index])
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> {
                check(xi == yj && xj == yk)
                for (j in 0 until yj) {
                    for (k in 0 until yk) {
                        val indexJK = j * yk + k
                        for (i in 0 until yi) {
                            val index = (i * yj + j) * yk + k
                            result[index] = block(this[indexJK], this[index])
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
    axis: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(size)
    when (axis) {
        0 -> {
            check(xi == other.size)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val index = (i * xj + j) * xk + k
                        result[index] = block(this[index], other[i])
                    }
                }
            }
        }

        1 -> {
            check(xj == other.size)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val index = (i * xj + j) * xk + k
                        result[index] = block(this[index], other[j])
                    }
                }
            }
        }

        2 -> {
            check(xk == other.size)
            for (i in 0 until xi) {
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val index = (i * xj + j) * xk + k
                        result[index] = block(this[index], other[k])
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
    axis1: Int,
    axis2: Int,
    block: (Float, Float) -> Float,
): DataBuffer {
    val result = DataBuffer.create(size)
    when (axis1) {
        0 -> when (axis2) {
            1 -> {
                check(xi == yi && xj == yj)
                for (i in 0 until xi) {
                    for (j in 0 until xj) {
                        val indexIJ = i * xj + j
                        for (k in 0 until xk) {
                            val index = (i * xj + j) * xk + k
                            result[index] = block(this[index], other[indexIJ])
                        }
                    }
                }
            }

            2 -> {
                check(xi == yi && xk == yj)
                for (i in 0 until xi) {
                    for (k in 0 until xk) {
                        val indexIK = i * xk + k
                        for (j in 0 until xj) {
                            val index = (i * xj + j) * xk + k
                            result[index] = block(this[index], other[indexIK])
                        }
                    }
                }
            }
        }

        1 -> when (axis2) {
            2 -> {
                check(xj == yi && xk == yj)
                for (j in 0 until xj) {
                    for (k in 0 until xk) {
                        val indexJK = j * xk + k
                        for (i in 0 until xi) {
                            val index = (i * xj + j) * xk + k
                            result[index] = block(this[index], other[indexJK])
                        }
                    }
                }
            }
        }
    }
    return result
}

internal inline fun DataBuffer.reduce(operation: (Float, Float) -> Float): Float {
    var acc = this[0]
    for (i in 1 until size) {
        acc = operation(acc, this[i])
    }
    return acc
}

internal inline fun DataBuffer.reduce(xi: Int, xj: Int, axis: Int, operation: (Float, Float) -> Float): DataBuffer =
    when (axis) {
        0 -> {
            val result = DataBuffer.create(size = xj)
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
            val result = DataBuffer.create(size = xi)
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

internal inline fun DataBuffer.reduce(
    xi: Int,
    xj: Int,
    xk: Int,
    axis: Int,
    operation: (Float, Float) -> Float,
): DataBuffer = when (axis) {
    0 -> {
        val result = DataBuffer.create(xj * xk)
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
        val result = DataBuffer.create(xi * xk)
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
        val result = DataBuffer.create(xi * xj)
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
