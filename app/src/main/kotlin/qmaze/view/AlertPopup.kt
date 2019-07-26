package qmaze.view

import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.DialogPane
import javafx.scene.shape.Rectangle
import javafx.scene.web.WebView
import java.io.IOException

fun popupAlert(message: String, content: String) {
    val alert = Alert(Alert.AlertType.ERROR)
    alert.title = "Bad News"
    alert.headerText = message
    val r = Rectangle(50.0, 50.0)
    r.fill = AGENT_DEATH
    alert.graphic = r
    alert.contentText = content
    alert.showAndWait()
}

fun popupInstructions(){
    val dp = DialogPane()

    val info = Dialog<String>()
    info.width = 350.0
    info.height = 600.0
    info.isResizable = true
    info.title = "Instructions"

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
    info.dialogPane = dp
    info.showAndWait()
}
