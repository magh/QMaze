package qmaze.controller

import org.junit.Test
import qmaze.agent.Agent
import qmaze.environment.Array2D
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.Room

/**
 * @author katharine
 * This is really just hear so I can debug it and test manually.
 */
class EpisodeTest {

    /**
     * Test of play method, of class Episode.
     */
    @Test
    fun testPlay() {
        //setup
        val maze_size = 4
        val start = Coordinate(0, 0)
        val goalState = Coordinate(maze_size - 1, maze_size - 1)
        val agent = Agent(start, 0.1, 0.1, 0.1)
        val rooms = Array2D<Room>(maze_size, maze_size, Room())
        val maze = Maze(rooms, start, goalState)
        val ep = Episode(agent, maze)
        //test
        ep.play()
    }

}
