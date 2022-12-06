package pro.evanwright.govee.exception

/**
 * Thrown when an invalid parameter is passed to a control endpoint.
 */
class GoveeInvalidParameterException(message: String) : GoveeAPIException(message)