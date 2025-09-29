import com.wsr.IOType

fun main() {
    val sample = listOf(
        IOType.d2(listOf(3, 4)) { x, y ->
            (x * 4) + y.toDouble() + 1
        },
        IOType.d2(listOf(3, 4)) { x, y ->
            (x * 4) + y.toDouble() + 13
        },
    )
    println(sample.im2col(kernel = 3).map { it.toList() })
//    measureTime {
//        createMnistModel(1, 3)
//    }.also(::println)
}

private fun List<IOType.D2>.im2col(
    kernel: Int,
    stride: Int = 1,
    padding: Int = 0,
): Array<DoubleArray> {
    val (channel, inputSize) = first().shape
    val output = (inputSize - kernel + 2 * padding) / stride + 1
    val result = Array(this.size * output) { DoubleArray(kernel * channel) }
    this.forEachIndexed { index, ioType ->
        for (o in 0 until output) {
            val row = index * output + o
            for (k in 0 until kernel) {
                val inputIndex = o * stride + k - padding
                if (inputIndex in 0 until inputSize) {
                    for (c in 0 until channel) {
                        result[row][c * kernel + k] = ioType[c, inputIndex]
                    }
                }
            }
        }
    }
    return result
}
