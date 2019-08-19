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
class Agent(
    startingState: Coordinate,
    private var probabilityExplore: Double,
    private val learningRate: Double,
    private val rewardDiscount: Double
) {

    val memory: AgentMemory = AgentMemory(startingState)

    fun haltExploring(): Double {
        val originalEpsilon = probabilityExplore
        probabilityExplore = 0.0
        return originalEpsilon
    }

    fun resumeExploring(originalEpsilon: Double){
        this.probabilityExplore = originalEpsilon
    }

    fun location(): Coordinate {
        return memory.currentState
    }

    fun chooseAction(nextAvailableActions: List<Coordinate>): Coordinate {
        if (nextAvailableActions.isEmpty()) {
            throw NoWhereToGoException(location())
        }
        return if (Math.random() < probabilityExplore) {
            pickRandomAction(nextAvailableActions)
        } else {
            pickBestActionOrRandom(nextAvailableActions)
        }
    }

    fun takeAction(actionTaken: Coordinate, reward: Double) {
        val currentQValue = memory.rewardFromAction(location(), actionTaken)

        // two step reward calc
        var estimatedBestFutureReward = 0.0
        val actionsForFutureState = memory.actionsForState(actionTaken)
        if (actionsForFutureState.isNotEmpty()) {
            val maxRewardFromSubequentAction = pickBestActionOrRandom(actionsForFutureState)
            estimatedBestFutureReward = memory.rewardFromAction(actionTaken, maxRewardFromSubequentAction)
        }

        val qValue = learningRate * (reward + rewardDiscount * estimatedBestFutureReward - currentQValue)
        memory.updateMemory(actionTaken, qValue)
        memory.move(actionTaken)
    }

    private fun pickRandomAction(actions: List<Coordinate>): Coordinate {
        val choice = (Math.random() * actions.size.toDouble()).toInt()
        return actions[choice]
    }

    private fun pickBestActionOrRandom(actions: List<Coordinate>): Coordinate {
        var bestActions: MutableList<Coordinate> = ArrayList()
        var highestReward = 0.0

        for (action in actions) {
            val rewardMemory = memory.rewardFromAction(location(), action)
            if (rewardMemory > highestReward) {
                highestReward = rewardMemory
                bestActions = ArrayList()
                bestActions.add(action)
            }

            if (rewardMemory == highestReward) {
                bestActions.add(action)
            }
        }
        return pickRandomAction(bestActions)
    }

    fun introduceSelf(startingState: Coordinate) {
        println("I'm training with probabilityExplore: $probabilityExplore rewardDiscount: $rewardDiscount and learningRate: $learningRate\nStaring at $startingState")
    }

}

class NoWhereToGoException(state: Coordinate) : Exception("I have no-where to go from here: $state")
