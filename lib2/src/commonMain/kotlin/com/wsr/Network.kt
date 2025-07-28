package com.wsr

import com.wsr.common.IOType1d

class Network2(private val layers: List<Layer<IOType1d>>) {
    fun expect(input: IOType1d): IOType1d =
        layers.fold(input) { acc, layer -> layer.expect(acc) }

    fun train(input: IOType1d, label: IOType1d) {
//        layers[0].train(input) { o0 ->
//            layers[1].train(o0) { o1 ->
//                layers[2].train(o1) { o2 ->
//                    layers[3].train(o2) { o3 ->
//                        layers[4].train(o3) { o4 ->
//                            layers[5].train(o4) { o5 ->
//                                Array(o5.size) { o5[it] - label[it] } }
//                            }
//                        }
//                }
//            }
//        }
        val deltaList = mutableListOf({ input: IOType1d -> Array(input.size) { input[it] - label[it] } })
        for (index in layers.lastIndex downTo 0) {
            val i = deltaList.lastIndex
            deltaList.add { input: IOType1d -> layers[index].train(input, deltaList[i]) }
        }
        deltaList.last().invoke(input)
    }
}
