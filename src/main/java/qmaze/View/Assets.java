package qmaze.View;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

/**
 * @author katharine
 * Essentially a config class for themes etc. Ish.
 */
public class Assets {

    private static Assets assets = new Assets();

    private Assets() {
    }

    public static Assets getInstance() {
        return assets;
    }

    String learningPanelBackground = "-fx-background-color: #e4f9db";
    String unvisitedRoom = "-fx-background-color: #f2f9ef";
    String goldBackground = "-fx-background-color: #FFFF9A";
    String whiteBackground = "-fx-background-color: #ffffff";
    String buttonPanelBackground = "-fx-background-color: #a5ea8a;";

    public ImagePattern getAgentImage() {
        String path = "/agent.png";
        return new ImagePattern(new Image(path));
    }

    public ImagePattern getAgentAtGoalImage() {
        String path = "/agentAtGoal.png";
        return new ImagePattern(new Image(path));
    }

    public ImagePattern getAgentDeathImage() {
        String path = "/agentDeath.png";
        return new ImagePattern(new Image(path));
    }

    public ImagePattern getGoalImage() {
        String path = "/goal.png";
        return new ImagePattern(new Image(path));
    }

    public String getLightGreenBackground() {
        return learningPanelBackground;
    }

    public String getUnvisitedRoomBackground() {
        return unvisitedRoom;
    }

    public String getGoalRoomBackground() {
        return goldBackground;
    }

    public String getWhiteBackground() {
        return whiteBackground;
    }

    public String getRichGreenBackground() {
        return buttonPanelBackground;
    }

    public ObservableList<String> getAgentOptions() {
        return FXCollections.observableArrayList(
                "Robot",
                "Sheep"
        );
    }

}
