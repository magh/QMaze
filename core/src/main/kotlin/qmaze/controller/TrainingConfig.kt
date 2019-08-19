package qmaze.controller

/**
 *
 * @author katharine
 */
data class TrainingConfig(
    val rewardDiscount: Double,
    val probabilityExplore: Double,
    val learningRate: Double
)
