package com.wsr.layer.reshape.token

import com.wsr.IOType
import com.wsr.layer.reshape.Reshape

class TokenEmbeddingD1ToD2(
    override val outputX: Int,
    override val outputY: Int,
) : Reshape.D1ToD2() {
    override fun expect(input: List<IOType.D1>): List<IOType.D2> {
        TODO("Not yet implemented")
    }

    override fun train(
        input: List<IOType.D1>,
        calcDelta: (List<IOType.D2>) -> List<IOType.D2>,
    ): List<IOType.D1> {
        TODO("Not yet implemented")
    }
}
