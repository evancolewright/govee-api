package pro.evanwright.govee.exception

/**
 * Thrown when a checked exception is thrown.
 */
class GoveeWrappedException(throwable: Throwable) : GoveeAPIException(throwable)