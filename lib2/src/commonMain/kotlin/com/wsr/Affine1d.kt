package com.wsr

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.set

class Affine1d(
    numOfInput: Int,
    private val numOfNeuron: Int,
) : Layer<D1> {
    private var weight: D2Array<Double> = mk.ndarray(List(numOfInput) { List(numOfNeuron) { 0.0 } })
    private var input: D1Array<Double>? = null
    override fun forward(input: D1Array<Double>): D1Array<Double> {
        this.input = input
        return weight.dot(input)
    }

    override fun backward(delta: D1Array<Double>): D1Array<Double> {
        delta.dot(input!!.transpose(0))
        weight.transpose(0, 1).dot(delta)
        TODO()
    }
}