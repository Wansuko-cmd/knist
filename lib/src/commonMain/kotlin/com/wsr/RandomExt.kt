package com.wsr

import kotlin.random.Random

fun Random.nextFloat(from: Float, until: Float) = nextDouble(from.toDouble(), until.toDouble()).toFloat()
