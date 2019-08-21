package qmaze.view

import qmaze.controller.Game
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.getAdjoiningRooms

class MazeGame(val maze: Maze) : Game<Coordinate> {

    override fun getStart(): Coordinate {
        return maze.start
    }

    override fun getGoal(): Coordinate {
        return maze.goal
    }

    override fun getReward(action: Coordinate): Double {
        return maze.getRoom(action)!!.reward
    }

    override fun getActions(location: Coordinate): List<Coordinate> {
        return getAdjoiningRooms(location, maze.rooms)
    }

}
