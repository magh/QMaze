package qmaze.agent

import java.util.HashMap

/**
 * @author katharine
 * The Agent learns as it moves through the mazeController:
 * - What room am I in? Co-ordinates.
 * - Is there a reward for moving into this room?
 * Then it can recall:
 * - What was the reward for moving into this room?
 * - What are the best rooms I remember, the next step on?
 *
 * Have to: initialise class
 * then set starting state before anything else can happen.
 * Why not do this in the constructor? We use the memory for multiple episodes.
 */
class AgentMemory<T>(var currentState: T) {

    private val mazeMemory: MutableMazeMemory<T> = HashMap()

    fun updateMemory(action: T, reward: Double) {
        // get possible actions
        val nextSteps = mazeMemory[currentState].orEmpty().toMutableMap()
        // get current reward for action
        val rewardMemory = nextSteps[action] ?: 0.0
        // increase reward for action
        nextSteps[action] = rewardMemory + reward
        // update memory
        mazeMemory[currentState] = nextSteps
    }

    fun move(action: T) {
        currentState = action
    }

    //What do I remember about future actions>
    fun actionsForState(state: T): List<T> {
        val nextSteps = mazeMemory[state].orEmpty()
        return nextSteps.keys.toList()
    }

    fun rewardFromAction(state: T, action: T): Double {
        //Nope, no memory of next steps or moving into this room.
        return mazeMemory[state]?.get(action) ?: 0.0
    }

}

typealias MazeMemory<T> = Map<T, Map<T, Double>>

typealias MutableMazeMemory<T> = MutableMap<T, Map<T, Double>>
