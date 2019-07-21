package qmaze.view.mazecomponents

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.util.Duration
import qmaze.environment.Coordinate
import qmaze.environment.Room
import qmaze.view.ViewController
import qmaze.view.components.ControllerState
import java.util.ArrayList
import java.util.HashMap

private const val ANIMATION_INTERVAL: Long = 500

/**
 * @author katharine
 */
class QMazeGridController(controller: ViewController) {

    var rooms: MutableList<Room>? = null

    var rows: Int = 0
        private set

    var columns: Int = 0
        private set

    var startingState: Coordinate? = null

    var goalState: Coordinate? = null

    var agentLocation: Coordinate? = null
        private set

    init {
        initialiseMazeRooms(controller)
    }

    private fun initialiseMazeRooms(controller: ViewController) {
        val mazeConfig = controller.getTrainingConfig()
        this.rows = mazeConfig.rows
        this.columns = mazeConfig.columns
        if (startingState == null) {
            this.startingState = Coordinate(0, 0)
        }
        if (goalState == null) {
            this.goalState = Coordinate(columns - 1, rows - 1)
        }

        this.rooms = ArrayList()
        for (i in 0 until columns) {
            for (j in 0 until rows) {
                val room = Room(Coordinate(i, j))
                rooms!!.add(room)
            }
        }
    }

    fun reset(controller: ViewController) {
        agentLocation = null
        if (controller.state === ControllerState.RESET_STATE || controller.state === ControllerState.ADJUST_PARAM_STATE) {
            //Reset maze, according to controller's instructions
            initialiseMazeRooms(controller)
        }
        if (controller.state === ControllerState.TRAINED_STATE) {
            //Show heatmap
            val heatMap = controller.learningController.heatMap
            showVisitCount(heatMap, controller)
        }
        if (controller.state === ControllerState.ADJUST_MAZE_STATE) {
            //Clear heatmap
            showVisitCount(HashMap(), controller)
        }
        controller.redrawMaze()
    }

    /**
     * Animation/heatmap stuff
     */
    private fun showVisitCount(heatMap: Map<Coordinate, Int>?, controller: ViewController) {
        if (heatMap == null) {
            return
        }
        //Get max visit count
        val maxVisit = getTotalVisitCount(heatMap)
        val highestVisit = getHighestVisitCount(heatMap)

        for (room in rooms!!) {
            val roomLocation = room.coordinate
            if (heatMap.containsKey(roomLocation)) {
                heatMap[roomLocation]?.let {
                    room.percentageVisits = it / maxVisit
                    room.visitCount = it / highestVisit
                }
            } else {
                room.percentageVisits = 0.0
                room.visitCount = 0.0
            }
        }
        controller.redrawMaze()
    }

    private fun getTotalVisitCount(heatMap: Map<Coordinate, Int>): Double {
        var totalVisits = 0.0
        val keys = heatMap.keys
        for (key in keys) {
            heatMap[key]?.let {
                totalVisits += it
            }
        }
        return totalVisits
    }

    private fun getHighestVisitCount(heatMap: Map<Coordinate, Int>): Double {
        var highestVisit = 0
        val keys = heatMap.keys
        for (key in keys) {
            heatMap[key]?.let {
                if (it > highestVisit) {
                    highestVisit = it
                }
            }
        }
        return highestVisit.toDouble()
    }

    fun animateMap(optimalPath: List<Coordinate>, controller: ViewController) {

        animateAgent(startingState, controller)

        println("Finding path")
        val stepsToGoal = optimalPath.size
        val interval = getInterval(stepsToGoal)
        println("Interval is: $interval")
        var timeMillis: Long = 0
        for (agentLocation in optimalPath) {
            val beat = Timeline(
                KeyFrame(
                    Duration.millis(timeMillis.toDouble()),
                    EventHandler<ActionEvent> { animateAgent(agentLocation, controller) }
                )
            )
            beat.isAutoReverse = true
            beat.cycleCount = 1
            beat.play()
            timeMillis += interval
        }
    }

    private fun animateAgent(roomWithAgent: Coordinate?, controller: ViewController) {
        agentLocation = roomWithAgent
        controller.redrawMaze()
    }

    private fun getInterval(stepsToGoal: Int): Long {

        println("Steps are: $stepsToGoal")
        var interval = ANIMATION_INTERVAL
        //Whole animation should take around 30 seconds or less. If there are more than 6000
        // steps, which is highly unlikely, but anyway, don't bother because the
        // human eye wont see it (and your laptop has probably died by now).
        if (stepsToGoal > 6000) {
            throw RuntimeException("Too many steps to display")
        }
        if (stepsToGoal > 30) {
            //So if we have more than 60 steps to the optimal path,
            // then we want the interval to be smaller to accomodate this
            val millisAvailable = (30 * 1000).toLong()
            interval = millisAvailable / stepsToGoal.toLong()
        }
        return interval
    }

}
