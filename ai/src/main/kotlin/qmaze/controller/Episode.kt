package qmaze.controller

import qmaze.agent.Agent
import qmaze.agent.NoWhereToGoException
import java.util.ArrayList

private const val STEP_LIMIT = 5000

/**
 *
 * @author katharine
 * The events that join the agent with the environment
 */
class Episode<T>(val agent: Agent<T>, val game: Game<T>) {

    val episodeSteps: MutableList<T> = ArrayList()

    //Where is the agent?
    //Have a look around the mazeController
    //Decide on action
    fun nextAction(): T {
        try {
            val adjoiningRooms = game.getActions(agent.location())
            return agent.chooseAction(adjoiningRooms)
        } catch (e: NoWhereToGoException) {
            throw EpisodeInterruptedException(e, episodeSteps.size)
        }
    }

    fun play() {
        agent.memory.move(game.getStart())
        recordSteps(game.getStart())
        while (!atGoalState()) {
            val action = nextAction()

            //Did the mazeController give a reward?
            val reward = game.getReward(action)
            agent.takeAction(action, reward)

            recordSteps(action)
        }
        println("Finished episode with ${episodeSteps.size} steps.")
    }

    fun recordSteps(action: T) {
        episodeSteps.add(action)
        if (episodeSteps.size == STEP_LIMIT) {
            throw EpisodeInterruptedException("taking too long!", episodeSteps.size)
        }
    }

    fun atGoalState(): Boolean {
        return agent.location() == game.getGoal()
    }

}

class EpisodeInterruptedException : Exception {

    constructor(e: Exception, step: Int) : super("Episode interrupted at step ${step} due to ${e.message}")

    constructor(message: String, step: Int) : super("Episode interrupted at step ${step} due to ${message}")
}
