package qmaze.controller

interface Game<T> {
    // getAdjoiningRooms(location())
    fun getActions(location: T): List<T>
    fun getStart(): T
    fun getGoal(): T
    // maze.getRoom(action)!!.reward
    fun getReward(action: T): Double
}
