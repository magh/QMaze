package qmaze.view

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.util.Duration
import qmaze.controller.LearningController
import qmaze.controller.TrainingConfig
import qmaze.controller.TrainingInterruptedException
import qmaze.environment.Array2D
import qmaze.environment.Coordinate
import qmaze.environment.Maze
import qmaze.environment.Room
import qmaze.view.ViewController.ControllerState.*
import tornadofx.clear
import java.util.*

const val initialGamma = 0.7
const val initialEpsilon = 0.1
const val initialAlpha = 0.1
const val initialRows = 4
const val initialColumns = 4
const val initialEpisodes = 50

private const val ANIMATION_INTERVAL: Long = 500

/**
 * @author katharine
 * 2 types of component: animated and non-animated.
 * - The non-animated (e.g. buttons, sliders, Q Learning panel) are all treated the same.
 * - The animated (mazeController) is a special case, built by the controller and directly managed.
 * The ComponentController:
 * - Acts as a go between for LearningController and components
 * - Manages state across the Components.
 */
class ViewController {

    //Playing with mazeController rooms, no need to resize but need to disable optimal path etc ;
    enum class ControllerState {
        RESET_STATE, //("Reset"), //Hard reset everything to initial values
        TRAINED_STATE, //("Trained"), //We have trained the algorithm, so we can show optimal path, heatmap
        ADJUST_PARAM_STATE, //("PreTrain"), //Playing with variables, so need to resize mazeController, etc
        ADJUST_MAZE_STATE //("PreTrainNoAdjust")
    }

    val mazeSpinnerRows = SimpleIntegerProperty(initialRows)
    val mazeSpinnerColumns = SimpleIntegerProperty(initialColumns)
    val mazeSpinnerEpisodes = SimpleIntegerProperty(initialEpisodes)

    private val sliderGamma = SimpleDoubleProperty(initialGamma)
    private val sliderEpsilon = SimpleDoubleProperty(initialEpsilon)
    private val sliderAlpha = SimpleDoubleProperty(initialAlpha)

    lateinit var qTableGrid: GridPane
    lateinit var mazeRoomGrid: GridPane

    private var learningController: LearningController? = null

    private var agentLocation: Coordinate? = null

    private var maze: Maze? = null

    // roomCoordinate, roomVisitCount
    private var heatMap: Map<Coordinate, Int>? = null

    init {
        mazeSpinnerColumns.addListener { _, _, _ ->
            resetConfig()
        }
        mazeSpinnerRows.addListener { _, _, _ ->
            resetConfig()
        }
        mazeSpinnerEpisodes.addListener { _, _, _ ->
            resetEpisodes()
        }
    }

    fun resetHard() {
        resetComponents(RESET_STATE)
    }

    private fun resetRoom() {
        resetComponents(ADJUST_MAZE_STATE)
    }

    private fun resetConfig() {
        resetComponents(ADJUST_PARAM_STATE)
    }

    private fun resetEpisodes() {
        resetComponents(ADJUST_MAZE_STATE)
    }

    private fun resetComponents(state: ControllerState) {
        agentLocation = null
        if (state === RESET_STATE || state === ADJUST_PARAM_STATE) {
            //Reset mazeController, according to controller's instructions
            initialiseMazeRooms()
        }
        if (state === ADJUST_MAZE_STATE || state === RESET_STATE) {
            //Clear heatmap
            heatMap = HashMap()
        }
        redrawMaze()
        redrawQTable()
    }

    private fun redrawQTable() {
        qTableGrid.clear()
        learningController?.getLearnings()?.entries.orEmpty().forEach {
            val textPane = Pane()
            val roomCoordinate = it.key
            val columnIndex = it.key.x
            val rowIndex = it.key.y
            val sb = StringBuilder()
            sb.append("Room ").append(rowIndex).append(",").append(columnIndex).append("\n")
            val actions = it.value
            var toolTipText = ""
            when {
                maze?.goal == roomCoordinate -> {
                    sb.append("GOAL")
                    toolTipText = "Yay!"
                    textPane.style = "-fx-background-color: #FFFF9A"
                }
                actions.isEmpty() -> {
                    sb.append("No info")
                    toolTipText = "Maybe we didn't visit this room?"
                    textPane.style = "-fx-background-color: #f2f9ef"
                }
                else -> for (entry in actions.entries) {
                    val (arrow, desc) = getArrowDescDirection(roomCoordinate, entry.key)
                    val qValueForText = String.format("%.2f", entry.value)
                    sb.append(qValueForText)
                    sb.append(arrow)
                    sb.append("\n")
                    textPane.style = "-fx-background-color: #ffffff"
                    val qValueForToolTip = String.format("%.4f", entry.value)
                    toolTipText += "Moving $desc for $qValueForToolTip\n"
                }
            }
            val t = Text(sb.toString())

            val tp = Tooltip(toolTipText)
            Tooltip.install(textPane, tp)

            textPane.children.add(t)
            textPane.setMinSize(60.0, 60.0)
            textPane.setMaxSize(70.0, 70.0)

            qTableGrid.add(textPane, it.key.x, it.key.y)
        }
    }

    private fun getArrowDescDirection(currentRoom: Coordinate, nextRoom: Coordinate): Pair<String, String> {
        val currentRow = currentRoom.y
        val currentColumn = currentRoom.x
        val nextRow = nextRoom.y
        val nextColumn = nextRoom.x
        if (currentRow == nextRow && currentColumn > nextColumn) {
            return Pair(" <- ", "left")
        } else if (currentRow == nextRow && currentColumn < nextColumn) {
            return Pair(" -> ", "right")
        } else if (currentRow > nextRow && currentColumn == nextColumn) {
            return Pair(" ^ ", "up")
        } else if (currentRow < nextRow && currentColumn == nextColumn) {
            return Pair(" v ", "down")
        }
        return Pair(nextRoom.toString(), nextRoom.toString())
    }

    fun startTraining() {
        println("Training")
        try {
            learningController = maze?.let { LearningController(it, getTrainingConfig()) }
            heatMap = learningController?.startLearning()
            resetComponents(TRAINED_STATE)
        } catch (te: TrainingInterruptedException) {
            popupAlert(te.message!!, "There's no goal state I can get to. You're killing me!")
        }

    }

    fun showOptimalPath() {
        println("Finding optimal path...")
        learningController?.getOptimalPath()?.let {
            animateMap(it)
        }
    }

    val sliderEpsilonListener = ChangeListener<Number> { _, _, newValue ->
        sliderEpsilon.value = newValue.toDouble()
        resetConfig()
    }

    val sliderGammaListener = ChangeListener<Number> { _, _, newValue ->
        sliderGamma.value = newValue.toDouble()
        resetConfig()
    }

    val sliderAlphaListener = ChangeListener<Number> { _, _, newValue ->
        sliderAlpha.value = newValue.toDouble()
        resetEpisodes()
    }

    private fun getTrainingConfig(): TrainingConfig {
        return TrainingConfig(mazeSpinnerEpisodes.value, sliderGamma.value, sliderEpsilon.value, sliderAlpha.value)
    }

    private fun redrawMaze() {
        mazeRoomGrid.clear()

        //Get max visit count
        val maxVisit = heatMap?.let { getTotalVisitCount(it) }
        val highestVisit = heatMap?.let { getHighestVisitCount(it) }

        maze?.forEach { col, row, room ->
            val roomCoordinate = Coordinate(col, row)
            val r = Rectangle(50.0, 50.0)
            val open = room.open
            val hasAgent = roomCoordinate == agentLocation
            val percentageVisits = heatMap?.get(roomCoordinate)?.let { vc -> maxVisit?.let { mv -> vc / mv } } ?: 0.0
            val totalVisits = heatMap?.get(roomCoordinate)?.let { vc -> highestVisit?.let { hv -> vc / hv } } ?: 0.0
            val fillFactor = (percentageVisits + totalVisits) / 2
            val stack = StackPane()
            stack.shape = r
            val p = if (open) {
                Color.color(1 - fillFactor, 1.0, 1 - fillFactor)
            } else {
                Color.DARKGRAY
            }

            val bf = BackgroundFill(p, null, null)
            stack.background = Background(bf)

            val r2 = Rectangle(50.0, 50.0)

            if (roomCoordinate == maze?.goal) {
                if (hasAgent) {
                    r2.fill = AGENT_AT_GOAL
                } else {
                    r2.fill = GOAL
                }
                //r2.setOpacity(0.4);
            } else if (roomCoordinate == maze?.start && !hasAgent) {
                r2.opacity = 0.0
                stack.children.add(Label("X"))
            } else if (hasAgent) {
                r2.fill = AGENT
                //r2.setOpacity(0.4);
            } else {
                r2.opacity = 0.0
            }
            stack.children.add(r2)

            val tp = Tooltip("R:$row, C:$col, V:${(percentageVisits * 100).toInt()}%")
            Tooltip.install(stack, tp)

            stack.setOnMouseClicked { value ->
                val isStartingState = roomCoordinate == maze?.start
                val isGoalState = roomCoordinate == maze?.goal
                if (value.isControlDown) {
                    if (isStartingState || isGoalState) {
                        popupAlert("Trying to close this room?", "You can't close it!")
                    } else {
                        maze?.setRoom(col, row, room.copy(open = !room.open))
                        resetRoom()
                    }
                } else if (value.isShiftDown) {
                    println("Changing starting state to $roomCoordinate")
                    maze?.start = roomCoordinate
                    resetRoom()
                } else {
                    println("Changing goal state to $roomCoordinate")
                    maze?.let { m ->
                        m.getRoom(m.goal)?.copy(reward = 0.0)?.let { r ->
                            m.setRoom(m.goal.x, m.goal.y, r)
                        }
                        m.goal = roomCoordinate
                        m.getRoom(m.goal)?.copy(reward = 100.0)?.let { r ->
                            m.setRoom(m.goal.x, m.goal.y, r)
                        }
                    }
                    resetRoom()
                }
            }
            mazeRoomGrid.add(stack, col, row)
        }
    }

    private fun initialiseMazeRooms() {
        val columns = mazeSpinnerColumns.value
        val rows = mazeSpinnerRows.value
        maze = Maze(Array2D(columns, rows, Room()))
    }

    private fun getTotalVisitCount(heatMap: Map<Coordinate, Int>): Double {
        var totalVisits = 0.0
        for (key in heatMap.keys) {
            heatMap[key]?.let {
                totalVisits += it
            }
        }
        return totalVisits
    }

    private fun getHighestVisitCount(heatMap: Map<Coordinate, Int>): Double {
        var highestVisit = 0
        for (key in heatMap.keys) {
            heatMap[key]?.let {
                if (it > highestVisit) {
                    highestVisit = it
                }
            }
        }
        return highestVisit.toDouble()
    }

    private fun animateMap(optimalPath: List<Coordinate>) {

        animateAgent(maze?.start)

        println("Finding path")
        val stepsToGoal = optimalPath.size
        val interval = getInterval(stepsToGoal)
        println("Interval is: $interval")
        var timeMillis: Long = 0
        for (agentLocation in optimalPath) {
            val beat =
                    Timeline(KeyFrame(Duration.millis(timeMillis.toDouble()),
                            EventHandler<ActionEvent> { animateAgent(agentLocation) }))
            beat.isAutoReverse = true
            beat.cycleCount = 1
            beat.play()
            timeMillis += interval
        }
    }

    private fun animateAgent(roomWithAgent: Coordinate?) {
        agentLocation = roomWithAgent
        redrawMaze()
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
