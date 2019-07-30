package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.AgentLearningParameters
import qmaze.environment.Coordinate
import qmaze.environment.Maze

/**
 * @author katharine
 */
class OptimalEpisode(agent: Agent, maze: Maze, startingState: Coordinate) : Episode(agent, maze, startingState) {

    @Throws(EpisodeInterruptedException::class)
    fun findOptimalPath(): List<Coordinate> {
        val originalLearningParameters = agent.learningParameters
        haltExploration(originalLearningParameters)

        agent.start(startingState)
        episodeSteps.add(startingState)
        while (!atGoalState()) {
            val action = nextAction()
            agent.move(action)
            recordSteps(action)
            println("Moved to $action")
        }
        println("Found optimalPath in " + episodeSteps.size + " steps.")

        resumeExploration(originalLearningParameters)
        return episodeSteps
    }

    private fun haltExploration(originalLearningParameters: AgentLearningParameters) {
        val noExploreLearningParameters = AgentLearningParameters(
            0.0,
            originalLearningParameters.learningRate, originalLearningParameters.gamma
        )
        agent.learningParameters = noExploreLearningParameters
    }

    private fun resumeExploration(originalLearningParameters: AgentLearningParameters) {
        agent.learningParameters = originalLearningParameters
    }

}
