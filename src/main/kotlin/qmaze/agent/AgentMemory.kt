package qmaze.agent

import qmaze.environment.Coordinate
import java.util.ArrayList
import java.util.HashMap

/**
 * @author katharine
 * The Agent learns as it moves through the maze:
 * - What room am I in? Co-ordinates.
 * - Is there a reward for moving into this room?
 * Then it can recall:
 * - What was the reward for moving into this room?
 * - What are the best rooms I remember, the next step on?
 *
 *
 * Have to: initialise class
 * then set starting state before anything else can happen.
 * Why not do this in the constructor? We use the memory for multiple episodes.
 */
class AgentMemory {

    private val mazeMemory: MutableMap<Coordinate, Map<Coordinate, Double>>
    var currentState: Coordinate? = null

    init {
        mazeMemory = HashMap()
    }

    fun setStartingState(startingState: Coordinate) {
        this.currentState = startingState
    }

    /*
     * TO DO: code me!
     * For hints on the steps I need to take, see hints.txt
     */
    fun updateMemory(action: Coordinate, reward: Double) {
        val nextSteps = mazeMemory[currentState].orEmpty().toMutableMap()
        val rewardMemory = nextSteps[action] ?: 0.0

        nextSteps[action] = rewardMemory + reward
        this.mazeMemory[currentState!!] = nextSteps
    }

    fun move(action: Coordinate) {
        this.currentState = action
    }

    //What do I remember about future actions>
    fun actionsForState(state: Coordinate): ArrayList<Coordinate> {
        val nextSteps = mazeMemory[state] ?: return ArrayList()
        return ArrayList(nextSteps.keys)
    }

    fun rewardFromAction(state: Coordinate?, action: Coordinate): Double {
        //Nope, no memory of next steps or moving into this room.
        return mazeMemory[state]?.get(action) ?: return 0.0
    }
}
