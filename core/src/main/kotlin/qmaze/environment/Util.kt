package qmaze.environment

enum class Direction(val arrow: String, val desc: String) {
    UP("^", "up"),
    DOWN("v", "down"),
    LEFT("<", "left"),
    RIGHT(">", "right")
}

fun getArrowDescDirection(currentRoom: Coordinate, nextRoom: Coordinate): Direction {
    val currentRow = currentRoom.y
    val currentColumn = currentRoom.x
    val nextRow = nextRoom.y
    val nextColumn = nextRoom.x
    if (currentRow == nextRow && currentColumn > nextColumn) {
        return Direction.LEFT
    } else if (currentRow == nextRow && currentColumn < nextColumn) {
        return Direction.RIGHT
    } else if (currentRow > nextRow && currentColumn == nextColumn) {
        return Direction.UP
    } else if (currentRow < nextRow && currentColumn == nextColumn) {
        return Direction.DOWN
    }
    throw RuntimeException("Unknown direction ${currentRoom} ${nextRoom}")
}
