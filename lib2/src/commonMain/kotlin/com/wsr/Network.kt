package com.wsr

import com.wsr.common.IOType1d

class Network2(private val layers: List<Layer<IOType1d>>) {
    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOType1d, label: IOType1d) {
        val deltaList = mutableListOf({ input: IOType1d -> Array(input.size) { input[it] - label[it] } })
        for (index in layers.lastIndex downTo 0) {
            val i = deltaList.lastIndex
            deltaList.add { input: IOType1d -> layers[index].train(input, deltaList[i]) }
        }
        deltaList.last().invoke(input)
    }
}
