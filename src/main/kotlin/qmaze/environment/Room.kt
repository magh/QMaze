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
 * - About the maze
 */
class Room(val coordinate: Coordinate) {

    var open = true

    var reward = 0.0

    var percentageVisits = 0.0

    var visitCount = 0.0

    fun adjoins(otherRoom: Room): Boolean {
        val x_other = otherRoom.coordinate.xCoordinate
        val y_other = otherRoom.coordinate.yCoordinate
        val x_coordinate = coordinate.xCoordinate
        val y_coordinate = coordinate.yCoordinate
        return (x_other == x_coordinate && y_other == y_coordinate - 1
                || x_other == x_coordinate && y_other == y_coordinate + 1
                || y_other == y_coordinate && x_other == x_coordinate - 1
                || y_other == y_coordinate && x_other == x_coordinate + 1)
    }
}
