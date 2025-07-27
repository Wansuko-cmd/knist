package com.wsr

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus

class Bias1d(numOfInput: Int) : Layer<D1> {
    private var weight: D1Array<Double> = mk.ndarray(List(numOfInput) { 0.0 })
    override fun forward(input: D1Array<Double>): D1Array<Double> {
        return input + weight
    }

    override fun backward(delta: D1Array<Double>): D1Array<Double> {
        weight = weight - delta
        return delta
    }
}