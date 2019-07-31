/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qmaze.environment

import java.util.ArrayList

/**
 * @author katharine
 * A Room knows:
 * - Where it is
 * - If it is open or closed
 * - What reward (if any) it contains
 * - If another room is adjoining/ neighboring
 * The Room does not know:
 * - About the agent
 * - About the mazeController
 */
data class Room(val open: Boolean = true, val reward: Double = 0.0)

fun getAdjoiningRooms(state: Coordinate, rooms: Array2D<Room>): List<Coordinate> {
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

private fun adjoins(theRoom: Coordinate, otherRoom: Coordinate): Boolean {
    val xOther = otherRoom.x
    val yOther = otherRoom.y
    val xCoordinate = theRoom.x
    val yCoordinate = theRoom.y
    return (xOther == xCoordinate && yOther == yCoordinate - 1
            || xOther == xCoordinate && yOther == yCoordinate + 1
            || yOther == yCoordinate && xOther == xCoordinate - 1
            || yOther == yCoordinate && xOther == xCoordinate + 1)
}
