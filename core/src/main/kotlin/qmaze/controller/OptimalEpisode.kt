package qmaze.controller

import qmaze.agent.Agent
import qmaze.environment.Coordinate
import qmaze.environment.Maze

/**
 * @author katharine
 */
class OptimalEpisode(private val agent: Agent, private val maze: Maze) {

    private val episode: Episode = Episode(agent, maze)

    fun findOptimalPath(): List<Coordinate> {
        val originalEpsilon = haltExploration()

        agent.start(maze.start)
        episode.episodeSteps.add(maze.start)
        while (!episode.atGoalState()) {
            val action = episode.nextAction()
            agent.move(action)
            episode.recordSteps(action)
            println("Moved to $action")
        }
        println("Found optimalPath in ${episode.episodeSteps.size} steps.")

        resumeExploration(originalEpsilon)
        return episode.episodeSteps
    }

    private fun haltExploration(): Double {
        val originalEpsilon = agent.epsilon
        agent.epsilon = 0.0
        return originalEpsilon
    }

    private fun resumeExploration(epsilon: Double) {
        agent.epsilon = epsilon
    }

}
