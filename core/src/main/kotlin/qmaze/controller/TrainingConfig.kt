package qmaze.controller

/**
 *
 * @author katharine
 */
class TrainingConfig(
    val episodes: Int,
    val rows: Int,
    val columns: Int,
    val gamma: Double,
    val epsilon: Double,
    val alpha: Double
)
