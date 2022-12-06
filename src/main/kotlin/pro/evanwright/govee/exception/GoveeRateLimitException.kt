package pro.evanwright.govee.exception

/**
 * Thrown when the rate limit is exceeded.
 */
class GoveeRateLimitException(message: String) : GoveeAPIException(message)