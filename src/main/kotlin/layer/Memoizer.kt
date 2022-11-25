package layer

import java.util.concurrent.ConcurrentHashMap

class Memoizer<T, U> {
    private val cache = ConcurrentHashMap<T, U>()
    operator fun invoke(input: T, function: (T) -> U): U =
        cache.computeIfAbsent(input) {
            function(it)
        }
}
