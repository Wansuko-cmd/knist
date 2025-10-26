package com.wsr.layer.process.attention

import com.wsr.IOType
import com.wsr.layer.process.Process
import kotlinx.serialization.Serializable

@Serializable
class AttentionD2 internal constructor(
    override val outputX: Int,
    override val outputY: Int,
) : Process.D2() {
    override fun expect(input: List<IOType.D2>): List<IOType.D2> = TODO()

    override fun train(input: List<IOType.D2>, calcDelta: (List<IOType.D2>) -> List<IOType.D2>): List<IOType.D2> =
        TODO()
}
