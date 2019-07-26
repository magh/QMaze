package qmaze.agent

import qmaze.environment.Coordinate

import java.util.ArrayList

/**
 * Q(S(t), A(t)) ← Q(S(t), A(t)) + α [ R(t+1) + γ max Q(S(t+1), a) − Q(S(t), A(t)) ].
 *
 * @author katharine
 * I know about:
 * - My memory of learned rewards and possible actions
 * - My learning parameters
 * I am told about:
 * - The surrounding open rooms.
 * - If there is a reward in this room.
 * and use them to make decisions about which room to go in next.
 * I don't know:
 * - How many episodes I am trained for
 * I don't control:
 * - My movements overall - instead I am told to move at each step
 * and given information about the environment.
 */
class Agent(var epsilon: Double, private val learningRate: Double, private val gamma: Double) {

    val memory: AgentMemory = AgentMemory()

    fun location(): Coordinate? {
        return memory.currentState
    }

    fun start(startingState: Coordinate) {
        memory.setStartingState(startingState)
    }

    fun move(nextState: Coordinate) {
        memory.move(nextState)
    }

    @Throws(NoWhereToGoException::class)
    fun chooseAction(nextAvailableActions: List<Coordinate>): Coordinate {
        if (nextAvailableActions.isEmpty()) {
            throw NoWhereToGoException(memory.currentState!!)
        } else {
            return if (Math.random() < epsilon) {
                pickRandomAction(nextAvailableActions)
            } else {
                pickBestActionOrRandom(nextAvailableActions)
            }
        }
    }

    fun takeAction(actionTaken: Coordinate, reward: Double) {
        val currentQValue = memory.rewardFromAction(memory.currentState, actionTaken)
        var estimatedBestFutureReward = 0.0
        val actionsForFutureState = this.memory.actionsForState(actionTaken)
        if (actionsForFutureState.isNotEmpty()) {
            val maxRewardFromSubequentAction = this.pickBestActionOrRandom(actionsForFutureState)
            estimatedBestFutureReward = this.memory.rewardFromAction(actionTaken, maxRewardFromSubequentAction)
        }

        val qValue = learningRate * (reward + gamma * estimatedBestFutureReward - currentQValue)
        this.memory.updateMemory(actionTaken, qValue)
        this.memory.move(actionTaken)
    }

    private fun pickRandomAction(actions: List<Coordinate>): Coordinate {
        val options = actions.size
        val choice = (Math.random() * options.toDouble()).toInt()
        return actions[choice]
    }

    private fun pickBestActionOrRandom(actions: List<Coordinate>): Coordinate {
        var bestActions: MutableList<Coordinate> = ArrayList()
        var highestReward = 0.0

        for (action in actions) {
            val rewardMemory = this.memory.rewardFromAction(this.location(), action)
            if (rewardMemory > highestReward) {
                highestReward = rewardMemory
                bestActions = ArrayList()
                bestActions.add(action)
            }

            if (rewardMemory == highestReward) {
                bestActions.add(action)
            }
        }

        return this.pickRandomAction(bestActions)
    }

    fun introduceSelf(startingState: Coordinate) {
        println("I'm training with epsilon: $epsilon gamma: $gamma and alpha: $learningRate\nStaring at $startingState")
    }

}
