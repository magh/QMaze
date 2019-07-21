package qmaze.controller

import java.util.ArrayList

import qmaze.agent.Agent
import qmaze.agent.NoWhereToGoException
import qmaze.environment.Coordinate
import qmaze.environment.Maze

private const val STEP_LIMIT = 5000

/**
 *
 * @author katharine
 * The events that join the agent with the environment
 */
open class Episode(val agent: Agent, val maze: Maze, val startingState: Coordinate) {

    val episodeSteps: MutableList<Coordinate> = ArrayList()

    //Where is the agent?
    //Have a look around the maze
    //Decide on action
    //@Throws(EpisodeInterruptedException::class)
    fun nextAction(): Coordinate {
        try {
            agent.location()?.let {
                val adjoiningStates = maze.getAdjoiningStates(it)
                return agent.chooseAction(adjoiningStates)
            }
            throw EpisodeInterruptedException("Location not found", episodeSteps.size)
        } catch (e: NoWhereToGoException) {
            throw EpisodeInterruptedException(e, episodeSteps.size)
        }
    }

    @Throws(EpisodeInterruptedException::class)
    fun play() {
        agent.start(startingState)
        episodeSteps.add(startingState)
        while (!atGoalState()) {
            val action = nextAction()

            //Did the maze give a reward?
            val reward = maze.getReward(action)
            agent.takeAction(action, reward)

            recordSteps(action)
        }
        println("Finished episode with " + episodeSteps.size + " steps.")
    }

    @Throws(EpisodeInterruptedException::class)
    fun recordSteps(action: Coordinate) {
        episodeSteps.add(action)
        if (episodeSteps.size == STEP_LIMIT) {
            throw EpisodeInterruptedException("taking too long!", episodeSteps.size)
        }
    }

    fun atGoalState(): Boolean {
        return agent.location()?.let {
            maze.isGoalState(it)
        } ?: false
    }

}
