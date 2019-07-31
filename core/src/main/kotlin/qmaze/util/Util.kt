package qmaze.util

import qmaze.agent.MazeMemory
import qmaze.controller.getArrowDescDirection
import qmaze.environment.Coordinate

fun printHeatMap(xSize: Int, ySize: Int, heatMap: Map<Coordinate, Int>) {
    for (y in 0 until ySize) {
        for (x in 0 until xSize) {
            print("${heatMap[Coordinate(x, y)]} ")
        }
        println()
    }
}

fun printLearnings(
    xSize: Int,
    ySize: Int,
    learnings: MazeMemory
) {
    for (y in 0 until ySize) {
        for (x in 0 until xSize) {
            val currentRoom = Coordinate(x, y)
            val map = learnings[currentRoom]
            print("|")
            map?.forEach {
                val direction = getArrowDescDirection(currentRoom, it.key)
                if (it.value > 0.0) {
                    print("${direction.arrow}(${"%.4f".format(it.value)})")
                }
                print(" ")
            }
        }
        println()
    }
}

fun printOptimalPath(optimalPath: List<Coordinate>) {
    var old: Coordinate? = null
    optimalPath.forEach { c ->
        with(old) {
            if (this != null) {
                println(getArrowDescDirection(this, c))
            } else {
                println("START")
            }
        }
        old = c
    }
}
