package qmaze.view

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.scene.Node
import javafx.scene.control.Alert
import javafx.scene.control.ChoiceDialog
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import qmaze.controller.LearningController
import qmaze.controller.TrainingConfig
import qmaze.controller.TrainingInterruptedException
import qmaze.environment.Coordinate
import qmaze.environment.Room
import qmaze.view.components.ControllerState
import qmaze.view.components.popupAlert
import qmaze.view.mazecomponents.QMazeGridController
import tornadofx.clear

private const val SET_START = "Set starting room"
private const val SET_GOAL = "Set goal room"

const val initialGamma = 0.7
const val initialEpsilon = 0.1
const val initialAlpha = 0.1
const val initialRows = 4
const val initialColumns = 4
const val initialEpisodes = 50

/**
 * @author katharine
 * 2 types of component: animated and non-animated.
 * - The non-animated (e.g. buttons, sliders, Q Learning panel) are all treated the same.
 * - The animated (maze) is a special case, built by the controller and directly managed.
 * The ComponentController:
 * - Acts as a go between for LearningController and components
 * - Manages state across the Components.
 */
class ViewController {

    val mazeSpinnerRows = SimpleIntegerProperty(initialRows)
    val mazeSpinnerColumns = SimpleIntegerProperty(initialColumns)
    val mazeSpinnerEpisodes = SimpleIntegerProperty(initialEpisodes)

    private val sliderGamma = SimpleDoubleProperty(initialGamma)
    private val sliderEpsilon = SimpleDoubleProperty(initialEpsilon)
    private val sliderAlpha = SimpleDoubleProperty(initialAlpha)

    lateinit var qTableGrid: GridPane
    lateinit var mazeRoomGrid: GridPane

    val learningController: LearningController
    var state: ControllerState

    val maze: QMazeGridController

    init {
        this.state = ControllerState.RESET_STATE
        learningController = LearningController()
        maze = QMazeGridController(this)

        mazeSpinnerColumns.addListener { _, _, _ ->
            configReset()
        }
        mazeSpinnerRows.addListener { _, _, _ ->
            configReset()
        }
        mazeSpinnerEpisodes.addListener { _, _, _ ->
            episodesReset()
        }
    }

    /**
     * State Resets
     */
    private fun configReset() {
        this.state = ControllerState.ADJUST_PARAM_STATE
        resetComponents()
    }

    private fun episodesReset() {
        this.state = ControllerState.ADJUST_MAZE_STATE
        resetComponents()
    }

    private fun resetComponents() {
        maze.reset(this)
        resetQTable()
    }

    private fun resetQTable() {
        qTableGrid.clear()
        learningController.getLearnings().entries.forEach {
            qTableGrid.add(addNode(it, maze.goalState!!), it.key.xCoordinate, it.key.yCoordinate)
        }
    }

    private fun addNode(
        it: Map.Entry<Coordinate, Map<Coordinate, Double>>, goalState: Coordinate
    ): Node {
        val textPane = Pane()
        val roomCoordinate = it.key
        val columnIndex = it.key.xCoordinate
        val rowIndex = it.key.yCoordinate
        val sb = StringBuilder()
        sb.append("Room ").append(rowIndex).append(",").append(columnIndex).append("\n")
        val actions = it.value
        var toolTipText = ""
        if (goalState == roomCoordinate) {
            sb.append("GOAL")
            toolTipText = "Yay!"
            textPane.style = goldBackground //assets.getGoalRoomBackground());
        } else if (actions.isEmpty()) {
            sb.append("No info")
            toolTipText = "Maybe we didn't visit this room?"
            textPane.style = unvisitedRoom //assets.getUnvisitedRoomBackground());
        } else {
            for (entry in actions.entries) {
                val nextRoom = entry.key
                val qValueForText = String.format("%.2f", entry.value)
                sb.append(qValueForText)
                sb.append(getArrowDirection(roomCoordinate, nextRoom))
                sb.append("\n")
                textPane.style = whiteBackground //assets.getWhiteBackground());
                val qValueForToolTip = String.format("%.4f", entry.value)
                toolTipText = toolTipText + "Moving " + getDirectionDesc(
                    roomCoordinate, nextRoom
                ) + " for " + qValueForToolTip + "\n"
            }
        }
        val t = Text(sb.toString())

        val tp = Tooltip(toolTipText)
        Tooltip.install(textPane, tp)

        textPane.children.add(t)
        textPane.setMinSize(60.0, 60.0)
        textPane.setMaxSize(70.0, 70.0)
        return textPane
    }

    private fun getArrowDirection(currentRoom: Coordinate, nextRoom: Coordinate): String {
        val currentRow = currentRoom.yCoordinate
        val currentColumn = currentRoom.xCoordinate
        val nextRow = nextRoom.yCoordinate
        val nextColumn = nextRoom.xCoordinate
        if (currentRow == nextRow && currentColumn > nextColumn) {
            return " <- "
        } else if (currentRow == nextRow && currentColumn < nextColumn) {
            return " -> "
        } else if (currentRow > nextRow && currentColumn == nextColumn) {
            return " ^ "
        } else if (currentRow < nextRow && currentColumn == nextColumn) {
            return " v "
        }
        return nextRoom.toString()
    }

    private fun getDirectionDesc(currentRoom: Coordinate, nextRoom: Coordinate): String {
        val currentRow = currentRoom.yCoordinate
        val currentColumn = currentRoom.xCoordinate
        val nextRow = nextRoom.yCoordinate
        val nextColumn = nextRoom.xCoordinate
        if (currentRow == nextRow && currentColumn > nextColumn) {
            return "left"
        } else if (currentRow == nextRow && currentColumn < nextColumn) {
            return "right"
        } else if (currentRow > nextRow && currentColumn == nextColumn) {
            return "up"
        } else if (currentRow < nextRow && currentColumn == nextColumn) {
            return "down"
        }
        return nextRoom.toString()
    }

    fun hardReset() {
        this.state = ControllerState.RESET_STATE
        resetComponents()
    }

    private fun roomReset() {
        this.state = ControllerState.ADJUST_MAZE_STATE
        resetComponents()
    }

    /**
     * Actions
     */
    fun startTraining() {
        println("Training")
        val previousState = this.state
        try {
            this.state = ControllerState.TRAINED_STATE
            learningController.startLearning(
                maze.rooms!!.toList(), maze.rows, maze.columns, maze.startingState!!, getTrainingConfig()
            )
            resetComponents()
        } catch (te: TrainingInterruptedException) {
            showAlert(te.message!!)
            this.state = previousState
        }

    }

    fun showOptimalPath() {
        println("Finding optimal path...")
        val optimalPath = learningController.getOptimalPath(maze.startingState!!)
        maze.animateMap(optimalPath, this)
    }

    private fun showAlert(message: String) {
        popupAlert(message)
    }

    val sliderEpsilonListener = ChangeListener<Number> { _, _, newValue ->
        sliderEpsilon.value = newValue.toDouble()
        configReset()
    }

    val sliderGammaListener = ChangeListener<Number> { _, _, newValue ->
        sliderGamma.value = newValue.toDouble()
        configReset()
    }

    val sliderAlphaListener = ChangeListener<Number> { _, _, newValue ->
        sliderAlpha.value = newValue.toDouble()
        episodesReset()
    }

    fun getTrainingConfig(): TrainingConfig {
        return TrainingConfig(
            mazeSpinnerEpisodes.value,
            mazeSpinnerRows.value,
            mazeSpinnerColumns.value,
            sliderGamma.value,
            sliderEpsilon.value,
            sliderAlpha.value
        )
    }

    fun redrawMaze() {
        mazeRoomGrid.clear()
        maze.rooms?.forEach {
            mazeRoomGrid.add(addRoom(it, maze), it.coordinate.xCoordinate, it.coordinate.yCoordinate)
        }
    }

    private fun addRoom(
        room: Room, maze: QMazeGridController
    ): StackPane {
        if (room.coordinate == maze.goalState) {
            room.reward = 100.0
        } else {
            room.reward = 0.0
        }
        val columnIndex = room.coordinate.xCoordinate
        val rowIndex = room.coordinate.yCoordinate
        val r = Rectangle(50.0, 50.0)

        val open = room.open
        val hasAgent = room.coordinate == maze.agentLocation
        val percentageVisits = room.percentageVisits
        val totalVisits = room.visitCount
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

        if (room.coordinate == maze.goalState) {
            if (hasAgent) {
                r2.fill = AGENT_AT_GOAL
            } else {
                r2.fill = GOAL
            }
            //r2.setOpacity(0.4);
        } else if (room.coordinate == maze.startingState && !hasAgent) {
            r2.opacity = 0.0
            stack.children.add(Label("X"))
        } else if (hasAgent) {
            r2.fill = AGENT
            //r2.setOpacity(0.4);
        } else {
            r2.opacity = 0.0
        }
        stack.children.add(r2)

        val tp = Tooltip("R:$rowIndex, C:$columnIndex, V:${(percentageVisits * 100).toInt()}%")
        Tooltip.install(stack, tp)

        stack.setOnMouseClicked { value ->
            if (value.isControlDown) {
                if (room.coordinate == maze.startingState || room.coordinate == maze.goalState) {
                    val alert = Alert(Alert.AlertType.INFORMATION)
                    alert.title = "Oh dear"
                    alert.headerText = "Trying to close this room?"
                    alert.contentText = "You can't close it!"
                    alert.showAndWait()
                } else {
                    room.open = !room.open
                    redrawMaze()
                    roomReset()
                }
            } else {
                val options = FXCollections.observableArrayList(SET_START, SET_GOAL)
                val cd = ChoiceDialog(SET_START, options)
                cd.title = "Configure Rooms"
                cd.headerText = "Change the starting or goal room"
                val result = cd.showAndWait()
                result.ifPresent { selected ->
                    val isStartingState = room.coordinate == maze.startingState
                    val isGoalState = room.coordinate == maze.goalState
                    if (selected == SET_START && !isGoalState) {
                        println("Changing starting state to " + room.coordinate)
                        maze.startingState = room.coordinate
                        roomReset()
                    }
                    if (selected == SET_GOAL && !isStartingState) {
                        println("Changing goal state to " + room.coordinate)
                        maze.goalState = room.coordinate
                        roomReset()
                    }
                }
            }
        }
        return stack
    }

}
