package com.wsr.d1

import com.wsr.IOType

internal expect object OperatorExt : IOperatorExt

internal interface IOperatorExt {






    operator fun IOType.D1.div(other: Double) = IOType.d1(shape[0]) { this[it] / other }
}

operator fun IOType.D1.div(other: Double) = with(OperatorExt) { this@div.div(other) }
