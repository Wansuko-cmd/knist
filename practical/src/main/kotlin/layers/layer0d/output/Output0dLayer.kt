package layers.layer0d.output

import layers.IOType
import layers.Layer

interface Output0dLayer {
    fun toLayer(): List<Layer<IOType.IOType0d>>
}
