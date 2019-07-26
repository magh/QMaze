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
 * - Questions about state for specific locations in the mazeController.
 * Through the Maze we can control:
 * - Locations that are opened or closed
 * Shouldn't do:
 * - Reveal rooms.
 *
 * COLUMN IS X
 * ROW IS Y
 */
class Maze(private val rooms: Array2D<Room>,
           var start: Coordinate = Coordinate(0, 0),
           var goal: Coordinate = Coordinate(rooms.xSize - 1, rooms.ySize - 1)) {

    init {
        //TODO fix
        getRoom(goal)?.let {
            setRoom(goal.x, goal.y, it.copy(reward = 100.0))
        }
    }

    fun getRoom(state: Coordinate): Room? {
        return rooms.get(state.x, state.y)
    }

    fun setRoom(x: Int, y: Int, room: Room) {
        rooms.set(x, y, room)
    }

    fun forEach(block: (x: Int, y: Int, room: Room) -> Unit){
        rooms.forEach(block)
    }

    fun getXSize(): Int {
        return rooms.xSize
    }

    fun getYSize(): Int {
        return rooms.ySize
    }

    fun getAdjoiningStates(state: Coordinate): List<Coordinate> {
        val adjoiningRooms = ArrayList<Coordinate>()
        for (y in 0 until rooms.ySize) {
            for (x in 0 until rooms.xSize) {
                val c = Coordinate(x, y)
                val otherRoom = rooms.get(x, y)
                if (otherRoom.open && adjoins(state, c)) {
                    adjoiningRooms.add(c)
                }
            }
        }
        return adjoiningRooms
    }

}
