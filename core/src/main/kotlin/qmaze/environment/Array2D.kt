package qmaze.environment

class Array2D<T>(val xSize: Int, val ySize: Int, init: T) {

    private val array: MutableList<T> = ArrayList(xSize * ySize)

    init {
        repeat(xSize * ySize) {
            array.add(init)
        }
    }

    fun get(x: Int, y: Int): T {
        return array[y * xSize + x]
    }

    fun set(x: Int, y: Int, t: T) {
        array[y * xSize + x] = t
    }

    fun forEach(block: (x: Int, y: Int, t: T) -> Unit) {
        array.forEachIndexed { i, p ->
            block.invoke(i % xSize, i / xSize, p)
        }
    }

}
