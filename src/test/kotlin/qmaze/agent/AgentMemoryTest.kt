package qmaze.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import qmaze.environment.Coordinate
import java.util.*

/**
 *
 * @author katharine
 */
class AgentMemoryTest {

    /**
     * Test of updateMemory method, of class AgentMemory.
     */
    @Test
    fun testUpdateMemory() {

        val currentState = Coordinate(0, 0)
        val nextState = Coordinate(0, 1)
        val anotherState = Coordinate(1, 0)

        val reward = 0
        val memory = AgentMemory()
        memory.setStartingState(currentState)
        //Update memory when there is none

        memory.updateMemory(nextState, reward.toDouble())
        assertEquals(0.0, memory.rewardFromAction(currentState, nextState), 0.0)

        //Now update when there is some
        val increasedReward = 5
        memory.updateMemory(nextState, increasedReward.toDouble())
        assertEquals(increasedReward.toDouble(), memory.rewardFromAction(currentState, nextState), 0.0)
        //And increase it again
        memory.updateMemory(nextState, increasedReward.toDouble())
        assertEquals(
            (increasedReward + increasedReward).toDouble(),
            memory.rewardFromAction(currentState, nextState),
            0.0
        )

        //Try another unknown memory
        memory.updateMemory(anotherState, increasedReward.toDouble())
        assertEquals(increasedReward.toDouble(), memory.rewardFromAction(currentState, anotherState), 0.0)

        //And a new room
        memory.move(anotherState)
        memory.updateMemory(currentState, increasedReward.toDouble())
        assertEquals(increasedReward.toDouble(), memory.rewardFromAction(anotherState, currentState), 0.0)

    }

    @Test
    fun testBuildUpMemory() {
        //Move from SS -> S1 -> SS -> S2 -> GS
        //Should get memory: SS : S1,0 S2,0
        //S1 : SS,0
        //S2 : GS,100

        val startingState = Coordinate(0, 0)
        val stateOne = Coordinate(0, 1)
        val stateTwo = Coordinate(1, 0)
        val goalState = Coordinate(1, 0)

        //SS : S1,0
        val memory = AgentMemory()
        memory.setStartingState(startingState)
        var actionsFromStartingState: ArrayList<*> = memory.actionsForState(startingState)
        assertTrue(actionsFromStartingState.isEmpty())
        memory.updateMemory(stateOne, 0.0)
        actionsFromStartingState = memory.actionsForState(startingState)
        assertTrue(actionsFromStartingState.contains(stateOne))
        assertEquals(1, actionsFromStartingState.size.toLong())

        //S1 : SS,0
        assertEquals(0.0, memory.rewardFromAction(startingState, stateOne), 0.0)
        memory.move(stateOne)
        var actionsFromStateOne: ArrayList<*> = memory.actionsForState(stateOne)
        assertTrue(actionsFromStateOne.isEmpty())
        memory.updateMemory(startingState, 0.0)
        actionsFromStateOne = memory.actionsForState(stateOne)
        assertTrue(actionsFromStateOne.contains(startingState))
        assertEquals(1, actionsFromStateOne.size.toLong())

        //SS : S1,0 S2,0
        assertEquals(0.0, memory.rewardFromAction(stateOne, startingState), 0.0)
        memory.move(startingState)
        actionsFromStartingState = memory.actionsForState(startingState)
        assertTrue(actionsFromStartingState.contains(stateOne))
        memory.updateMemory(stateTwo, 0.0)
        actionsFromStartingState = memory.actionsForState(startingState)
        assertTrue(actionsFromStartingState.contains(stateTwo))
        assertEquals(2, actionsFromStartingState.size.toLong())
        assertEquals(0.0, memory.rewardFromAction(startingState, stateTwo), 0.0)
        memory.move(stateTwo)

        //S2 : GS,100
        var actionsFromStateTwo: ArrayList<*> = memory.actionsForState(stateTwo)
        assertTrue(actionsFromStateTwo.isEmpty())
        memory.updateMemory(goalState, 100.0)
        actionsFromStateTwo = memory.actionsForState(stateTwo)
        assertTrue(actionsFromStateTwo.contains(goalState))
        assertEquals(1, actionsFromStateTwo.size.toLong())
        assertEquals(100.0, memory.rewardFromAction(stateTwo, goalState), 0.0)
    }

}
