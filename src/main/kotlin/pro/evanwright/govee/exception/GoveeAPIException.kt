package pro.evanwright.govee.exception

import java.lang.RuntimeException

/**
 * Parent class for all GoveeAPI-related exceptions.
 */
open class GoveeAPIException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(throwable: Throwable) : super(throwable)
}
