package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.AgentLearningParameters
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.Room
import java.util.ArrayList
import java.util.HashMap

private const val EXCEPTION_THRESHOLD = 20

/**
 * @author katharine
 * I know about things I get from the view:
 * - Learning parameters
 * - Number of episodes
 * - The size of the maze
 * - If maze rooms are open or closed
 * I use the view data to configure the model.
 * I get these back from the model:
 * - Q values
 * - Optimal path
 * - Journey data
 * I pass information from the model to the view.
 */
class LearningController {

    private var maze: Maze? = null
    private var agent: Agent? = null
    var heatMap: MutableMap<Coordinate, Int>? = null

    @Throws(TrainingInterruptedException::class)
    fun startLearning(
        rooms: List<Room>,
        rows: Int,
        columns: Int,
        startingState: Coordinate,
        mazeConfig: TrainingConfig
    ) {

        var exceptionCount = 0
        heatMap = HashMap()
        initialiseMaze(rooms, rows, columns)
        initialiseAgent(mazeConfig)
        agent!!.introduceSelf(startingState)
        val episodes = mazeConfig.episodes

        for (i in 0 until episodes) {

            println("**Training episode $i")
            val e = Episode(agent!!, maze!!, startingState)
            try {
                e.play()
            } catch (ex: EpisodeInterruptedException) {
                println(ex.message)
                exceptionCount++
                if (exceptionCount > EXCEPTION_THRESHOLD) {
                    throw TrainingInterruptedException("I've exceeded the failure threshold.")
                }
            }

            buildHeatMap(e.episodeSteps)
        }
    }

    private fun initialiseMaze(rooms: List<Room>, rows: Int, columns: Int) {
        maze = Maze(rows, columns)
        rooms.forEach { r ->
            val roomLocation = Coordinate(r.coordinate.xCoordinate, r.coordinate.yCoordinate)
            val open = r.open
            maze!!.setOpen(roomLocation, open)
            if (r.reward > 0) {
                //TODO: make configurable, so more that one room can have a reward
                // So need a different way of signifying goal state
                maze!!.setGoalState(roomLocation, r.reward)
            }
        }
    }

    private fun initialiseAgent(mazeConfig: TrainingConfig) {
        val learningParameters = AgentLearningParameters(mazeConfig.epsilon, mazeConfig.alpha, mazeConfig.gamma)
        agent = Agent(learningParameters)
    }

    fun getOptimalPath(startingState: Coordinate): List<Coordinate> {
        val e = OptimalEpisode(agent!!, maze!!, startingState)
        var optimalPath: List<Coordinate> = ArrayList()

        try {
            optimalPath = e.findOptimalPath()
        } catch (ex: EpisodeInterruptedException) {
            println(ex.message)
        }

        return optimalPath
    }

    private fun buildHeatMap(episodeSteps: List<Coordinate>) {
        for (roomVisited in episodeSteps) {

            var roomVisitCount: Int? = heatMap!![roomVisited]
            if (roomVisitCount == null) {
                roomVisitCount = 0
            }
            roomVisitCount++
            heatMap!![roomVisited] = roomVisitCount
        }
    }

    fun getLearnings(): Map<Coordinate, Map<Coordinate, Double>> {
        maze?.let {
            return getLearnings(it.getRooms(), agent)
        }
        return HashMap()
    }

}

fun getLearnings(rooms: List<Room>, agent: Agent?): Map<Coordinate, Map<Coordinate, Double>> {
    val learning = HashMap<Coordinate, Map<Coordinate, Double>>()
    if(agent == null) {
        return learning
    }
    val memory = agent.memory
    rooms.forEach { r ->
        val roomLocation = r.coordinate
        if (r.open) {
            val rewardFromRoom = HashMap<Coordinate, Double>()
            val potentialActions = memory.actionsForState(roomLocation)
            for (action in potentialActions) {
                val reward = memory.rewardFromAction(roomLocation, action)
                rewardFromRoom[action] = reward
            }
            learning[roomLocation] = rewardFromRoom
        }
    }
    return learning
}
