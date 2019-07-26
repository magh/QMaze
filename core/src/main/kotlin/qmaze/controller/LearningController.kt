package qmaze.controller

import qmaze.agent.Agent
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import java.util.ArrayList
import java.util.HashMap

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
class LearningController(private val maze: Maze, private val mazeConfig: TrainingConfig) {

    private val agent = Agent(mazeConfig.epsilon, mazeConfig.alpha, mazeConfig.gamma)

    init {
        agent.introduceSelf(maze.start)
    }

    @Throws(TrainingInterruptedException::class)
    fun startLearning(): Map<Coordinate, Int> {
        var exceptionCount = 0

        val episodes = mazeConfig.episodes

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
        val e = OptimalEpisode(agent, maze)
        var optimalPath: List<Coordinate> = ArrayList()
        try {
            optimalPath = e.findOptimalPath()
        } catch (ex: EpisodeInterruptedException) {
            println(ex.message)
        }
        return optimalPath
    }

    fun getLearnings(): Map<Coordinate, Map<Coordinate, Double>> {
        val learnings = HashMap<Coordinate, Map<Coordinate, Double>>()
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
