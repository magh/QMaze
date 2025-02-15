package qmaze.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import qmaze.environment.Coordinate
import java.util.ArrayList

/**
 * @author katharine
 */
class AgentTest {

    /**
     * Test of start method, of class Agent.
     */
    @Test
    fun testStart() {
        val startingState = Coordinate(0, 0)
        val agent = Agent(startingState, 0.1, 0.1, 0.7)
        assertEquals(startingState, agent.location())
    }

    /**
     * Test of chooseAction method, of class Agent.
     */
    @Test
    fun testChooseActionWhenOnlyOne() {
        val startingState = Coordinate(0, 0)
        val nextAction = Coordinate(1, 0)
        val nextAvailableActions = ArrayList<Coordinate>()
        nextAvailableActions.add(nextAction)
        val agent = Agent(startingState, 0.1, 0.1, 0.7)
        val result = agent.chooseAction(nextAvailableActions)
        assertEquals(nextAction, result)
    }

    /**
     * Test of chooseAction method, of class Agent.
     */
    @Test
    fun testChooseActionWhenTwo() {
        val startingState = Coordinate(0, 0)
        val nextAction1 = Coordinate(1, 0)
        val nextAction2 = Coordinate(0, 1)
        val nextAvailableActions = ArrayList<Coordinate>()
        nextAvailableActions.add(nextAction1)
        nextAvailableActions.add(nextAction2)
        val agent = Agent(startingState, 0.1, 0.1, 0.7)
        val result = agent.chooseAction(nextAvailableActions)
        assertTrue(result == nextAction1 || result == nextAction2)
    }

    /**
     * Test of takeAction method, of class Agent.
     */
    @Test
    fun testTakeAction() {
        //Going SS -> S1 -> GS
        //Going SS -> S2 -> GS

        //Then SS -> S1 -> GS
        //and SS -> S2 -> GS

        val startingState = Coordinate(0, 0)
        val stateOne = Coordinate(0, 1)
        val stateTwo = Coordinate(1, 0)
        val goalState = Coordinate(1, 1)

        val agent = Agent(startingState, 0.1, 0.1, 0.7)

        //Going SS -> S1 -> GS
        agent.takeAction(stateOne, 0.0)
        assertEquals(stateOne, agent.location())

        agent.takeAction(goalState, 100.0)
        assertEquals(goalState, agent.location())

        assertEquals(0.0, agent.memory.rewardFromAction(startingState, stateOne), 0.0)
        assertEquals(10.0, agent.memory.rewardFromAction(stateOne, goalState), 0.0)

        //Going SS -> S2 -> GS
        agent.memory.move(startingState)
        agent.takeAction(stateTwo, 0.0)
        assertEquals(stateTwo, agent.location())
        agent.takeAction(goalState, 100.0)
        assertEquals(goalState, agent.location())

        agent.memory.move(startingState)
        assertEquals(0.0, agent.memory.rewardFromAction(startingState, stateTwo), 0.0)
        assertEquals(0.0, agent.memory.rewardFromAction(startingState, stateOne), 0.0)

        agent.memory.move(stateTwo)
        assertEquals(10.0, agent.memory.rewardFromAction(stateTwo, goalState), 0.0)

        //Then SS -> S1 -> GS
        agent.memory.move(startingState)
        agent.takeAction(stateOne, 0.0)
        assertEquals(stateOne, agent.location())
        agent.takeAction(goalState, 100.0)
        assertEquals(goalState, agent.location())
        assertEquals(0.0, agent.memory.rewardFromAction(startingState, stateTwo), 0.0)
        assertEquals(0.7, agent.memory.rewardFromAction(startingState, stateOne), 0.01)
        assertEquals(19.0, agent.memory.rewardFromAction(stateOne, goalState), 0.0)

        //Then SS -> S2 -> GS
        agent.memory.move(startingState)
        agent.takeAction(stateTwo, 0.0)
        assertEquals(stateTwo, agent.location())
        agent.takeAction(goalState, 100.0)
        assertEquals(goalState, agent.location())
        assertEquals(0.7, agent.memory.rewardFromAction(startingState, stateTwo), 0.01)
        assertEquals(0.7, agent.memory.rewardFromAction(startingState, stateOne), 0.01)

        assertEquals(19.0, agent.memory.rewardFromAction(stateTwo, goalState), 0.0)
    }

}
