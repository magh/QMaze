package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.MazeMemory
import qmaze.agent.MutableMazeMemory
import qmaze.controller.LearningController.Direction.*
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import java.util.*

private const val EXCEPTION_THRESHOLD = 20

/**
 * @author katharine
 * I know about things I get from the view:
 * - Learning parameters
 * - Number of episodes
 * - The size of the mazeController
 * - If mazeController rooms are open or closed
 * I use the view data to configure the model.
 * I get these back from the model:
 * - Q values
 * - Optimal path
 * - Journey data
 * I pass information from the model to the view.
 */
class LearningController(private val maze: Maze, private val episodes: Int, mazeConfig: TrainingConfig) {

    enum class Direction(val arrow: String, val desc: String) {
        UP("^", "up" ),
        DOWN("v", "down"),
        LEFT("<", "left"),
        RIGHT(">", "right")
    }

    private val agent = Agent(maze.start, mazeConfig.probabilityExplore, mazeConfig.learningRate, mazeConfig.rewardDiscount)

    init {
        agent.introduceSelf(maze.start)
    }

    fun startLearning(): Map<Coordinate, Int> {
        var exceptionCount = 0

        val heatMap = HashMap<Coordinate, Int>()

        for (i in 0 until episodes) {

            println("**Training episode $i")
            val e = Episode(agent, maze)
            try {
                e.play()
            } catch (ex: EpisodeInterruptedException) {
                println(ex.message)
                exceptionCount++
                if (exceptionCount > EXCEPTION_THRESHOLD) {
                    throw TrainingInterruptedException("I've exceeded the failure threshold.")
                }
            }

            for (roomVisited in e.episodeSteps) {
                var roomVisitCount = heatMap[roomVisited]
                if (roomVisitCount == null) {
                    roomVisitCount = 0
                }
                roomVisitCount++
                heatMap[roomVisited] = roomVisitCount
            }
        }
        return heatMap.toMap()
    }

    fun getOptimalPath(): List<Coordinate> {
        val originalEpsilon = agent.haltExploring()

        val episode = Episode(agent, maze)

        //episode.play()
        agent.memory.move(maze.start)
        episode.recordSteps(maze.start)
        while (!episode.atGoalState()) {
            val action = episode.nextAction()
            agent.memory.move(action)
            episode.recordSteps(action)
            println("Moved to $action")
        }

        println("Found optimalPath in ${episode.episodeSteps.size} steps.")
        agent.resumeExploring(originalEpsilon)
        return episode.episodeSteps
    }

    fun getLearnings(): MazeMemory {
        val learnings: MutableMazeMemory = HashMap()
        agent.memory.let { memory ->
            for (y in 0 until maze.getYSize()) {
                for (x in 0 until maze.getXSize()) {
                    val room = maze.getRoom(Coordinate(x, y))
                    val roomLocation = Coordinate(x, y)
                    if (room?.open == true) {
                        val rewardFromRoom = HashMap<Coordinate, Double>()
                        val potentialActions = memory.actionsForState(roomLocation)
                        for (action in potentialActions) {
                            val reward = memory.rewardFromAction(roomLocation, action)
                            rewardFromRoom[action] = reward
                        }
                        learnings[roomLocation] = rewardFromRoom
                    }
                }
            }
        }
        return learnings
    }

}

fun getArrowDescDirection(currentRoom: Coordinate, nextRoom: Coordinate): LearningController.Direction {
    val currentRow = currentRoom.y
    val currentColumn = currentRoom.x
    val nextRow = nextRoom.y
    val nextColumn = nextRoom.x
    if (currentRow == nextRow && currentColumn > nextColumn) {
        return LEFT
    } else if (currentRow == nextRow && currentColumn < nextColumn) {
        return RIGHT
    } else if (currentRow > nextRow && currentColumn == nextColumn) {
        return UP
    } else if (currentRow < nextRow && currentColumn == nextColumn) {
        return DOWN
    }
    throw RuntimeException("Unknown direction ${currentRoom} ${nextRoom}")
}

class TrainingInterruptedException(message: String) : Exception(message)
