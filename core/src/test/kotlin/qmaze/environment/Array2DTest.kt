package qmaze.environment

import org.junit.Test
import kotlin.test.assertEquals

class Array2DTest {

    @Test
    fun testFor() {
        val xSize = 5
        val ySize = 3
        val array = Array2D(xSize, ySize, "X")
        var sb = StringBuilder()
        for (y in 0 until ySize) {
            for (x in 0 until xSize) {
                sb.append(array.get(x, y))
                print(array.get(x, y))
            }
            assertEquals(expected = "XXXXX", actual = sb.toString())
            sb = StringBuilder()
            println()
        }
    }

    @Test
    fun testForEach() {
        val xSize = 5
        val ySize = 3
        val array = Array2D(xSize, ySize, "X")
        var yOld = 0
        val sb = StringBuilder()
        array.forEach { x, y, t ->
            if (y > yOld) {
                yOld = y
                println()
            }
            print("$t($x,$y) ")
            if (y == 0) {
                sb.append("$t")
            }
        }
        assertEquals(expected = "XXXXX", actual = sb.toString())
    }

}
