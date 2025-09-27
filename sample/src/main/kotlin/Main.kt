import com.wsr.IOType
import com.wsr.d1.deConvD1
import com.wsr.d2.convD1
import dataset.mnist.createMnistModel
import kotlin.time.measureTime

fun main() {
    val a = IOType.d1(1.0, 2.0, 3.0)
    val b = IOType.d1(4.0, 5.0)
    println(a.deConvD1(b))
//    measureTime {
//        createMnistModel(1, 3)
//    }.also(::println)
}
