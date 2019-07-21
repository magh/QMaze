package qmaze.view.components

import javafx.scene.control.*
import javafx.scene.shape.Rectangle
import javafx.scene.web.WebView
import qmaze.view.AGENT_AT_GOAL
import qmaze.view.AGENT_DEATH
import java.io.IOException

fun popupAlert(message: String) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.title = "Bad News"
    alert.headerText = message
    val r = Rectangle(50.0, 50.0)
    r.fill = AGENT_DEATH
    alert.graphic = r
    alert.contentText = "There's no goal state I can get to. You're killing me!"
    alert.showAndWait()
}

fun popupInstructions(){
    val dp = DialogPane()

    val info = Dialog<String>()
    info.setWidth(350.0)
    info.setHeight(600.0)
    info.setResizable(true)
    info.setTitle("Instructions")

    dp.headerText = "Configuring the Maze"

    val r = Rectangle(50.0, 50.0)
    r.fill = AGENT_AT_GOAL
    dp.graphic = r
    val loginButtonType = ButtonType("Got it!", ButtonBar.ButtonData.OK_DONE)
    dp.buttonTypes.add(loginButtonType)

    val webView = WebView()
    try {
        object {}.javaClass.getResourceAsStream("/Instructions.txt").use { bis ->
            val bytes = bis.readAllBytes()
            webView.engine.loadContent(String(bytes))
        }
    } catch (e: IOException) {
        println(e.message)
    }

    dp.content = webView
    info.setDialogPane(dp)
    info.showAndWait()
}
