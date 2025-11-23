package com.wsr.layer.process.position

import com.wsr.NetworkBuilder
import com.wsr.batch.Batch
import com.wsr.batch.operation.plus.plus
import com.wsr.core.IOType
import com.wsr.core.d2
import com.wsr.layer.Context
import com.wsr.layer.process.Process
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlinx.serialization.Serializable

@Serializable
class PositionEncodeD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
    private val waveLength: Float,
) : Process.D2() {
    private val position by lazy {
        IOType.d2(outputX, outputY) { x, y ->
            if (y % 2 == 0) {
                sin(x / waveLength.pow(y / outputY.toFloat()))
            } else {
                cos(x / waveLength.pow((y - 1) / outputY.toFloat()))
            }
        }
    }

    override fun expect(input: Batch<IOType.D2>, context: Context): Batch<IOType.D2> = input + position

    override fun train(
        input: Batch<IOType.D2>,
        context: Context,
        calcDelta: (Batch<IOType.D2>) -> Batch<IOType.D2>,
    ): Batch<IOType.D2> {
        val output = input + position
        return calcDelta(output)
    }
}

fun <T> NetworkBuilder.D2<T>.positionEncode(waveLength: Float = 10000f) = addProcess(
    process = PositionEncodeD2(
        outputX = inputX,
        outputY = inputY,
        waveLength = waveLength,
    ),
)
