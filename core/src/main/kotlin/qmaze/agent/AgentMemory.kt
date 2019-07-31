package qmaze.agent

import qmaze.environment.Coordinate
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
class AgentMemory {

    private val mazeMemory: MutableMazeMemory = HashMap()

    var currentState: Coordinate? = null

    fun setStartingState(startingState: Coordinate) {
        this.currentState = startingState
    }

    fun updateMemory(action: Coordinate, reward: Double) {
        val nextSteps = mazeMemory[currentState].orEmpty().toMutableMap()
        val rewardMemory = nextSteps[action] ?: 0.0

        nextSteps[action] = rewardMemory + reward
        mazeMemory[currentState!!] = nextSteps
    }

    fun move(action: Coordinate) {
        currentState = action
    }

    //What do I remember about future actions>
    fun actionsForState(state: Coordinate): List<Coordinate> {
        val nextSteps = mazeMemory[state].orEmpty()
        return nextSteps.keys.toList()
    }

    fun rewardFromAction(state: Coordinate?, action: Coordinate): Double {
        //Nope, no memory of next steps or moving into this room.
        return mazeMemory[state]?.get(action) ?: 0.0
    }

}

typealias MazeMemory = Map<Coordinate, Map<Coordinate, Double>>

typealias MutableMazeMemory = MutableMap<Coordinate, Map<Coordinate, Double>>
