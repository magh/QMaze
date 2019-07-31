package qmaze.controller

import org.junit.Test

import qmaze.environment.Array2D
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.Room
import qmaze.util.printHeatMap
import qmaze.util.printLearnings
import qmaze.util.printOptimalPath

class LearningControllerTest {

    @Test
    fun testLearning() {
        val xSize = 4
        val ySize = 4
        val rooms = Array2D(xSize, ySize, Room())
        val maze = Maze(rooms)
        val trainingConfig = TrainingConfig(50, 0.7, 0.1, 0.1)
        val lc = LearningController(maze, trainingConfig)
        val heatMap = lc.startLearning()
        printHeatMap(xSize, ySize, heatMap)
        printLearnings(xSize, ySize, lc.getLearnings())
        printOptimalPath(lc.getOptimalPath())
    }

}
