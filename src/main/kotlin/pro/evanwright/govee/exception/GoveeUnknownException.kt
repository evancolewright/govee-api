package pro.evanwright.govee.exception

/**
 * Thrown when the underlying API issue is unknown.
 */
class GoveeUnknownException(message: String) : GoveeAPIException(message)