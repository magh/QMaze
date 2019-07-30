package qmaze.agent

import qmaze.environment.Coordinate

/**
 *
 * @author katharine
 */
class NoWhereToGoException(state: Coordinate) : Exception("I have no-where to go from here: $state")
