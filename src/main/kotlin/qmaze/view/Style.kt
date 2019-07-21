package qmaze.view

import javafx.scene.image.Image
import javafx.scene.paint.ImagePattern

fun getBackground(color: String):String{
    return "-fx-background-color: $color"
}

//LightGreen
const val learningPanelBackground = "-fx-background-color: #e4f9db"

const val unvisitedRoom = "-fx-background-color: #f2f9ef"

//GoalRoom
const val goldBackground = "-fx-background-color: #FFFF9A"

const val whiteBackground = "-fx-background-color: #ffffff"

//RichGreen
const val buttonPanelBackground = "-fx-background-color: #a5ea8a;"

val AGENT = ImagePattern(Image("/agent.png"))
val AGENT_DEATH = ImagePattern(Image("/agentDeath.png"))
val AGENT_AT_GOAL = ImagePattern(Image("/agentAtGoal.png"))
val GOAL = ImagePattern(Image("/goal.png"))
