package com.wsr.process.output.layer0d

import com.wsr.common.iotype.IOType0d
import com.wsr.process.Layer

interface Output0dLayer {
    fun toLayer(): List<Layer<IOType0d>>
}
