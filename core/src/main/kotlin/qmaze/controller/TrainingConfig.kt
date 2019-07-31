package qmaze.controller

/**
 *
 * @author katharine
 */
data class TrainingConfig(
    val episodes: Int,
    val gamma: Double,
    val epsilon: Double,
    val alpha: Double
)
