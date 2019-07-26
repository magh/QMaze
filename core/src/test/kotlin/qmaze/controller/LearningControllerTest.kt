package qmaze.controller

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import qmaze.environment.Array2D
import qmaze.environment.Maze
import qmaze.environment.Room

class LearningControllerTest {

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getHeatMap() {
        val rooms = Array2D<Room>(4, 4, Room())
        val maze = Maze(rooms)
        val trainingConfig = TrainingConfig(50, 0.7, 0.1, 0.1)
        val lc = LearningController(maze, trainingConfig)
        val heatMap = lc.startLearning()
        println(heatMap)
        println(lc.getLearnings())
    }

    @Test
    fun startLearning() {
    }

    @Test
    fun getOptimalPath() {
    }

    @Test
    fun getLearnings() {
    }
}