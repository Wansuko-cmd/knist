package com.wsr.core.collection.index

import com.wsr.core.IOType
import com.wsr.core.get
import com.wsr.core.set
import kotlin.random.Random

fun IOType.D1.topK(k: Int, random: Random = Random): Int {
    val target = maxWithIndex()
        .takeWhile { it.first >= 0 }
        .take(k)
        .toList()

    return target.randomIndex(random)
}

fun IOType.D1.topP(p: Float, random: Random = Random): Int {
    val total = maxWithIndex()
        .takeWhile { it.first >= 0 }
        .sumOf { it.first.toDouble() }

    var sum = 0.0
    val threshold = total * p

    val target = maxWithIndex()
        .takeWhile { it.first >= 0 }
        .takeWhile { (value, _) ->
            val shouldTake = sum < threshold
            sum += value
            shouldTake
        }
        .toList()
        .ifEmpty { listOf(maxWithIndex().first()) }

    return target.randomIndex(random)
}

private fun IOType.D1.maxWithIndex(): Sequence<Pair<Float, Int>> = sequence {
    val target = copyOf()
    repeat(target.size) {
        var index = 0
        var max = Float.MIN_VALUE
        for (i in 0 until shape[0]) {
            if (max < target[i]) {
                index = i
                max = target[i]
            }
        }
        target[index] = Float.MIN_VALUE
        yield(max to index)
    }
}

private fun List<Pair<Float, Int>>.randomIndex(random: Random): Int {
    val sum = sumOf { (value, _) -> value.toDouble() }
    if (sum == 0.0) return random(random).second

    var p = random.nextDouble(from = 0.0, until = sum)
    for ((value, index) in this) {
        p -= value
        if (p <= 0) return index
    }
    return last().second
}
