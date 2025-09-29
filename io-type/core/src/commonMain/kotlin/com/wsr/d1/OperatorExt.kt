package com.wsr.d1

import com.wsr.IOType

internal expect object OperatorExt : IOperatorExt

internal interface IOperatorExt {


    operator fun IOType.D1.minus(other: IOType.D1) = IOType.d1(shape[0]) { this[it] - other[it] }



    operator fun List<IOType.D1>.minus(other: IOType.D1) = List(size) { this[it] - other }



    operator fun List<IOType.D1>.minus(other: List<IOType.D1>) = List(size) { this[it] - other[it] }

    operator fun Double.times(other: IOType.D1) = IOType.d1(other.shape[0]) { this * other[it] }

    operator fun Double.times(other: List<IOType.D1>) = List(other.size) { this * other[it] }

    operator fun IOType.D1.div(other: Double) = IOType.d1(shape[0]) { this[it] / other }
}

operator fun IOType.D1.minus(other: IOType.D1) = with(OperatorExt) { this@minus.minus(other) }

operator fun List<IOType.D1>.minus(other: IOType.D1) = with(OperatorExt) { this@minus.minus(other) }

operator fun List<IOType.D1>.minus(other: List<IOType.D1>) = with(OperatorExt) { this@minus.minus(other) }

operator fun Double.times(other: IOType.D1) = with(OperatorExt) { this@times.times(other) }

operator fun Double.times(other: List<IOType.D1>) = with(OperatorExt) { this@times.times(other) }

operator fun IOType.D1.div(other: Double) = with(OperatorExt) { this@div.div(other) }
