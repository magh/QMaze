package qmaze.controller

import qmaze.agent.Agent
import qmaze.environment.Coordinate
import qmaze.environment.Maze

/**
 * @author katharine
 */
class OptimalEpisode(agent: Agent, maze: Maze) : Episode(agent, maze) {

    @Throws(EpisodeInterruptedException::class)
    fun findOptimalPath(): List<Coordinate> {
        val originalEpsilon = agent.epsilon
        haltExploration()

        agent.start(maze.start)
        episodeSteps.add(maze.start)
        while (!atGoalState()) {
            val action = nextAction()
            agent.move(action)
            recordSteps(action)
            println("Moved to $action")
        }
        println("Found optimalPath in " + episodeSteps.size + " steps.")

        resumeExploration(originalEpsilon)
        return episodeSteps
    }

    private fun haltExploration() {
        agent.epsilon = 0.0
    }

    private fun resumeExploration(epsilon: Double) {
        agent.epsilon = epsilon
    }

}
