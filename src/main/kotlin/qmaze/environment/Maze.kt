package qmaze.environment

import java.util.ArrayList

/**
 * TODO: consistently do x,y or coordinate
 * @author katharine
 * The Maze knows:
 * - It's size
 * - The rooms it contains
 * - Where the start and end (goal) is
 * The Maze does not know:
 * - About the agent
 * Through the Maze we can ask:
 * - Questions about state for specific locations in the maze.
 * Through the Maze we can control:
 * - Locations that are opened or closed
 * Shouldn't do:
 * - Reveal rooms.
 *
 * COLUMN IS X
 * ROW IS Y
 */
class Maze(rows: Int, columns: Int) {

    private val rooms: MutableList<Room>

    private var goal: Room? = null

    init {
        this.rooms = ArrayList()
        buildMaze(rows, columns)
    }

    fun getRooms(): List<Room> {
        return rooms.toList()
    }

    private fun getRoom(state: Coordinate): Room? {
        for (room in rooms) {
            if (room.coordinate == state) {
                return room
            }
        }
        return null
    }

    fun setOpen(state: Coordinate, open: Boolean) {
        val r = getRoom(state)
        r!!.open = open
    }

    fun setGoalState(state: Coordinate, reward: Double) {
        goal = getRoom(state)
        goal!!.reward = reward
    }

    fun isGoalState(state: Coordinate): Boolean {
        val room = getRoom(state)
        return room == goal
    }

    fun getReward(action: Coordinate): Double {
        val r = getRoom(action)
        return r!!.reward
    }

    private fun buildMaze(rows: Int, columns: Int) {
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val r = Room(Coordinate(column, row))
                rooms.add(r)
            }
        }
    }

    fun getAdjoiningStates(state: Coordinate): List<Coordinate> {
        val r = getRoom(state)
        val adjoiningRooms = ArrayList<Coordinate>()
        rooms.stream().filter { otherRoom -> otherRoom.open && otherRoom.adjoins(r!!) }
            .forEachOrdered { otherRoom -> adjoiningRooms.add(otherRoom.coordinate) }
        return adjoiningRooms
    }

}
