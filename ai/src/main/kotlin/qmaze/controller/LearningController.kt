package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.AgentMemory
import qmaze.agent.MazeMemory
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
class LearningController<T>(
    private val game: Game<T>, private val episodes: Int,
    rewardDiscount: Double,
    probabilityExplore: Double,
    learningRate: Double
) {

    private val agent =
        Agent(game.getStart(), probabilityExplore, learningRate, rewardDiscount)

    init {
        agent.introduceSelf(game.getStart())
    }

    fun startLearning(): Map<T, Int> {
        var exceptionCount = 0

        val heatMap = HashMap<T, Int>()

        for (i in 0 until episodes) {

            println("**Training episode $i")
            val e = Episode(agent, game)
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

    fun getOptimalPath(): List<T> {
        val originalEpsilon = agent.haltExploring()

        val episode = Episode(agent, game)

        //episode.play()
        agent.memory.move(game.getStart())
        episode.recordSteps(game.getStart())
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

    fun getLearnings(block: (AgentMemory<T>) -> MazeMemory<T>): MazeMemory<T> {
        return block(agent.memory)
    }

}

class TrainingInterruptedException(message: String) : Exception(message)
