/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package qmaze.environment

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

fun adjoins(theRoom: Coordinate, otherRoom: Coordinate): Boolean {
    val x_other = otherRoom.x
    val y_other = otherRoom.y
    val x_coordinate = theRoom.x
    val y_coordinate = theRoom.y
    return (x_other == x_coordinate && y_other == y_coordinate - 1
            || x_other == x_coordinate && y_other == y_coordinate + 1
            || y_other == y_coordinate && x_other == x_coordinate - 1
            || y_other == y_coordinate && x_other == x_coordinate + 1)
}
