package common

fun <T : Comparable<T>> List<T>.maxIndex(): Int =
    this.foldIndexed(null) { index: Int, acc: Pair<Int, T>?, element: T ->
        when {
            acc == null -> index to element
            acc.second > element -> acc
            else -> index to element
        }
    }?.first ?: throw Exception()

inline fun <T, R> List<T>.mapDownIndexed(transform: (index: Int, T) -> R): List<R> {
    var index = this.size - 1
    val destination = ArrayList<R>()
    for (item in this.reversed()) {
        destination.add(transform(index--, item))
    }
    return destination.reversed()
}
