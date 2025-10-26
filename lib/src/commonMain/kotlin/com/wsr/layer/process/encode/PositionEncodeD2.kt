package com.wsr.layer.process.encode

import com.wsr.IOType
import com.wsr.NetworkBuilder
import com.wsr.layer.process.Process
import com.wsr.operator.plus
import kotlinx.serialization.Serializable
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

@Serializable
class PositionEncodeD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = input.map { input ->
        input + IOType.d2(input.shape) { x, y ->
            if (y % 2 == 0) {
                sin(x / 10000.0.pow(y / input.shape[1].toDouble()))
            } else {
                cos(x / 10000.0.pow(y / input.shape[1].toDouble()))
            }
        }
    }

    override fun train(
        input: List<IOType.D2>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D2> {
        val output = input.map { input ->
            input + IOType.d2(input.shape) { x, y ->
                if (y % 2 == 0) {
                    sin(x / 10000.0.pow(y / input.shape[1].toDouble()))
                } else {
                    cos(x / 10000.0.pow(y / input.shape[1].toDouble()))
                }
            }
        }
        return calcDelta(output)
    }
}

fun <T> NetworkBuilder.D2<T>.positionEncode() = addProcess(
    process = PositionEncodeD2(outputX = inputX, outputY = inputY),
)
