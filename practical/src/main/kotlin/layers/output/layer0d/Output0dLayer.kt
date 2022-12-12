package layers.output.layer0d

import common.iotype.IOType
import common.iotype.IOType0d
import layers.Layer

interface Output0dLayer {
    fun toLayer(): List<Layer<IOType0d>>
}
