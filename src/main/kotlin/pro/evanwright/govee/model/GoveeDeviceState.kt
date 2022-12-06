package pro.evanwright.govee.model

import java.awt.Color

/**
 * Represents the real-time state of a [GoveeDevice]
 */
data class GoveeDeviceState(
    val isOnline: Boolean,
    val powerState: String,
    val brightness: Int,
    val color: Color
)