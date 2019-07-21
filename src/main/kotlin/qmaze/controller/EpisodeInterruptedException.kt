package qmaze.controller

/**
 *
 * @author katharine
 */
class EpisodeInterruptedException : Exception {

    constructor(e: Exception, step: Int) : super("Episode interrupted at step ${step} due to ${e.message}") {}

    constructor(message: String, step: Int) : super("Episode interrupted at step ${step} due to $message") {}
}
