package com.wsr.layers.output.layer0d

import com.wsr.common.iotype.IOType
import com.wsr.common.iotype.IOType0d
import com.wsr.layers.Layer

interface Output0dLayer {
    fun toLayer(): List<Layer<IOType0d>>
}
