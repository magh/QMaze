package qmaze.view

import javafx.beans.value.ChangeListener
import javafx.geometry.Orientation
import javafx.scene.image.Image
import javafx.scene.layout.Border
import javafx.scene.layout.HBox
import javafx.scene.paint.ImagePattern
import tornadofx.App
import tornadofx.View
import tornadofx.action
import tornadofx.bind
import tornadofx.borderpane
import tornadofx.button
import tornadofx.flowpane
import tornadofx.gridpane
import tornadofx.hbox
import tornadofx.insets
import tornadofx.label
import tornadofx.scrollpane
import tornadofx.slider
import tornadofx.spinner
import tornadofx.stackpane
import tornadofx.text

const val WIDTH = 1200.0
const val HEIGHT = 600.0

val AGENT = ImagePattern(Image("/agent.png"))
val AGENT_DEATH = ImagePattern(Image("/agentDeath.png"))
val AGENT_AT_GOAL = ImagePattern(Image("/agentAtGoal.png"))
val GOAL = ImagePattern(Image("/goal.png"))

class QMazeView : View() {
    override val root = stackpane {
        title = "Q Learning"
        prefWidth = WIDTH
        prefHeight = HEIGHT

        borderpane {
            val controller = ViewController()

            top = flowpane {
                orientation = Orientation.HORIZONTAL
                padding = insets(5, 0, 5, 0)
                vgap = 4.0
                hgap = 4.0
                style = "-fx-background-color: #a5ea8a;"

                hbox {
                    padding = insets(15, 12, 15, 12)
                    spacing = 10.0

                    hbox {
                        button("Instructions") {
                            action(::popupInstructions)
                        }
                    }
                    hbox {
                        button("Start training") {
                            action(controller::startTraining)
                        }
                    }
                    hbox {
                        button("Reset") {
                            action(controller::resetHard)
                        }
                    }
                    //TODO enabled: controller.state === ControllerState.TRAINED_STATE
                    hbox {
                        button("Show optimal path") {
                            action(controller::showOptimalPath)
                        }
                    }
                }

                //children.add(controller.build(LearningParameterComponent(controller)))
                flowpane {
                    orientation = Orientation.VERTICAL
                    padding = insets(5, 0, 5, 0)
                    vgap = 4.0
                    hgap = 4.0
                    style = "-fx-background-color: #a5ea8a;"
                    maxHeight = 150.0
                    hbox {
                        add(slider("Probability Explore", initialEpsilon, controller.sliderEpsilonListener))
                        add(slider("Reward Discount", initialGamma, controller.sliderGammaListener))
                        add(slider("Learning Rate", initialAlpha, controller.sliderAlphaListener))
                    }
                    hbox {
                        padding = insets(15, 12, 15, 12)
                        spacing = 10.0
                        label("Rows")
                        spinner(2, 16, initialRows, 1, false, controller.mazeSpinnerRows)
                        label("Columns")
                        spinner(2, 16, initialColumns, 1, false, controller.mazeSpinnerColumns)
                        label("Episodes")
                        spinner(1, 250, initialEpisodes, 1, true, controller.mazeSpinnerEpisodes)
                    }
                }
            }
            // Q Table
            right = borderpane {
                top = text("Q Table")
                center = scrollpane {
                    controller.qTableGrid = gridpane {
                        maxWidth = WIDTH / 2
                        border = Border.EMPTY
                        hgap = 10.0
                        vgap = 10.0
                        padding = insets(10, 10, 10, 10)
                        style = "-fx-background-color: #e4f9db"
                    }
                    center = controller.qTableGrid
                }
            }
            // Maze rooms
            center = borderpane {
                top = text("Maze rooms")
                center = scrollpane {
                    content = borderpane {
                        controller.mazeRoomGrid = gridpane {
                            hgap = 10.0
                            vgap = 10.0
                            padding = insets(10, 10, 10, 10)
                        }
                        center = controller.mazeRoomGrid
                    }
                }
            }
        }
    }

    private fun slider(text: String, initialValue: Double, listener: ChangeListener<Number>): HBox {
        return hbox {
            padding = insets(15, 12, 15, 12)
            spacing = 10.0
            add(label(text))
            val slider = slider(0.0, 1.0, initialValue) {
                showTickLabelsProperty().value = true
                showTickMarksProperty().value = true
                majorTickUnit = 0.5
                blockIncrement = 0.1
            }
            //TODO "%.1f"
            label().bind(property = slider.valueProperty())
            slider.valueProperty().addListener(listener)
            add(slider)
        }
    }
}

class QMazeApp : App(QMazeView::class)
