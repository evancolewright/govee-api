package pro.evanwright.govee.exception

/**
 * Thrown when the GoveeAPI has an internal server error.
 */
class GoveeInternalException(message: String) : GoveeAPIException(message)