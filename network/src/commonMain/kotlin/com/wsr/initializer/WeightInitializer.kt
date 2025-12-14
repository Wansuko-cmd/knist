package com.wsr.initializer

import com.wsr.core.IOType

interface WeightInitializer {
    fun d1(input: List<Int>, output: List<Int>, size: Int): IOType.D1
    fun d2(input: List<Int>, output: List<Int>, x: Int, y: Int): IOType.D2
    fun d3(input: List<Int>, output: List<Int>, x: Int, y: Int, z: Int): IOType.D3
    fun d4(input: List<Int>, output: List<Int>, i: Int, j: Int, k: Int, l: Int): IOType.D4
}
