package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.NoWhereToGoException
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.getAdjoiningRooms
import java.util.ArrayList

private const val STEP_LIMIT = 5000

/**
 *
 * @author katharine
 * The events that join the agent with the environment
 */
class Episode(val agent: Agent, val maze: Maze) {

    val episodeSteps: MutableList<Coordinate> = ArrayList()

    //Where is the agent?
    //Have a look around the mazeController
    //Decide on action
    fun nextAction(): Coordinate {
        try {
            val adjoiningRooms = getAdjoiningRooms(agent.location(), maze.rooms)
            return agent.chooseAction(adjoiningRooms)
        } catch (e: NoWhereToGoException) {
            throw EpisodeInterruptedException(e, episodeSteps.size)
        }
    }

    fun play() {
        agent.memory.move(maze.start)
        episodeSteps.add(maze.start)
        while (!atGoalState()) {
            val action = nextAction()

            //Did the mazeController give a reward?
            val reward = maze.getRoom(action)!!.reward
            agent.takeAction(action, reward)

            recordSteps(action)
        }
        println("Finished episode with ${episodeSteps.size} steps.")
    }

    fun recordSteps(action: Coordinate) {
        episodeSteps.add(action)
        if (episodeSteps.size == STEP_LIMIT) {
            throw EpisodeInterruptedException("taking too long!", episodeSteps.size)
        }
    }

    fun atGoalState(): Boolean {
        return agent.location() == maze.goal
    }

}
