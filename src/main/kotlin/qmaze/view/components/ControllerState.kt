package qmaze.view.components

//Playing with maze rooms, no need to resize but need to disable optimal path etc ;
enum class ControllerState {
    RESET_STATE, //("Reset"), //Hard reset everything to initial values
    TRAINED_STATE, //("Trained"), //We have trained the algorithm, so we can show optimal path, heatmap
    ADJUST_PARAM_STATE, //("PreTrain"), //Playing with variables, so need to resize maze, etc
    ADJUST_MAZE_STATE //("PreTrainNoAdjust")
}
