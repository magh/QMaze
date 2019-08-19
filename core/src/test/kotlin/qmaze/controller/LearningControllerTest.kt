package qmaze.controller

import org.junit.Test
import qmaze.environment.Array2D
import qmaze.environment.Maze
import qmaze.environment.Room
import qmaze.printHeatMap
import qmaze.printLearnings
import qmaze.printPath
import kotlin.test.assertEquals

class LearningControllerTest {

    @Test
    fun testLearning() {
        val xSize = 4
        val ySize = 4
        val rooms = Array2D(xSize, ySize, Room())
        val maze = Maze(rooms)
        val trainingConfig = TrainingConfig(0.7, 0.1, 0.1)
        val lc = LearningController(maze, 50, trainingConfig)
        val heatMap = lc.startLearning()
        printHeatMap(xSize, ySize, heatMap)
        printLearnings(xSize, ySize, lc.getLearnings())
        val optimalPath = lc.getOptimalPath()
        printPath(optimalPath)
        assertEquals(expected = 7, actual = optimalPath.size)
    }

}
