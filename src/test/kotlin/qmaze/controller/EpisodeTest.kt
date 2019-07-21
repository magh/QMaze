package qmaze.controller

import org.junit.Test
import qmaze.agent.Agent
import qmaze.agent.AgentLearningParameters
import qmaze.environment.Coordinate
import qmaze.environment.Maze

/**
 * @author katharine
 * This is really just hear so I can debug it and test manually.
 */
class EpisodeTest {

    /**
     * Test of play method, of class Episode.
     */
    @Test
    @Throws(Exception::class)
    fun testPlay() {
        //setup
        val startingState = Coordinate(0, 0)
        val maze_size = 4
        val goalState = Coordinate(maze_size - 1, maze_size - 1)
        val agentLP = AgentLearningParameters(0.1, 0.1, 0.1)
        val agent = Agent(agentLP)
        val maze = Maze(maze_size, maze_size)
        maze.setGoalState(goalState, 100.0)
        //test
        Episode(agent, maze, startingState).play()
    }

}
