package pro.evanwright.govee.model

import pro.evanwright.govee.CoreUtils
import pro.evanwright.govee.exception.GoveeAPIException
import pro.evanwright.govee.exception.GoveeInvalidParameterException
import pro.evanwright.govee.exception.GoveeWrappedException
import java.awt.Color
import java.io.IOException

data class GoveeDevice(
    val name: String,
    val model: String,
    val macAddress: String,
    val isControllable: Boolean,
    val isRetrievable: Boolean,
    val supportedCommands: List<String>,
    val colorTemperatureMin: Int,
    val colorTemperatureMax: Int
) {

    /**
     * Turns the [GoveeDevice] to the "on" state.
     * @throws GoveeAPIException  If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun turnOn() {
        sendCommand(CoreUtils.createSimpleCommandJson(this, "turn", "on"))
    }

    /**
     * Turns the [GoveeDevice] to the "off" state.
     * @throws GoveeAPIException  If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun turnOff() {
        sendCommand(CoreUtils.createSimpleCommandJson(this, "turn", "off"))
    }

    /**
     * Sets the brightness of the [GoveeDevice].
     * @param brightness  the brightness (1-100)
     * @throws GoveeAPIException If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun setBrightness(brightness: Int) {
        if (brightness > 100 || brightness < 1)
            throw GoveeInvalidParameterException("Brightness must be between 1 and 100!")
        sendCommand(CoreUtils.createSimpleCommandJson(this, "brightness", brightness))
    }

    /**
     * Sets the temperature of the [GoveeDevice].
     * @param temperature  A number between [GoveeDevice.colorTemperatureMin] and [GoveeDevice.colorTemperatureMax]
     * @throws GoveeAPIException If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun setTemperature(temperature: Int) {
        if (temperature > colorTemperatureMax || temperature < colorTemperatureMin)
            throw GoveeInvalidParameterException("Color temperature must be between $colorTemperatureMin and $colorTemperatureMax!")
        sendCommand(CoreUtils.createSimpleCommandJson(this, "temperature", temperature))
    }

    /**
     * Sets the color of the device from a [Color] object.
     * @param color  The intended color of the device
     * @throws GoveeAPIException If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun setColor(color: Color) {
        sendCommand(CoreUtils.createColorCommandJson(this, color))
    }

    /**
     * Sets the color of the device from a hexadecimal.
     * @param hexCode  The hex code of the color to set the device
     * @throws GoveeAPIException If the request fails
     */
    @Throws(GoveeAPIException::class)
    fun setColor(hexCode: String) {
        this.setColor(Color.decode(hexCode))
    }

    /**
     * Makes a request to retrieve the current state of the [GoveeDevice] and returns it.
     *
     * @return The real-time state of the device.
     * @throws GoveeAPIException If the request fails
     */
    fun getDeviceState(): GoveeDeviceState {
        try {
            CoreUtils.checkResponse(
                CoreUtils.get(
                    String.format(
                        "/devices/state?device=%s&model=%s",
                        macAddress.replace(":", "%3A"), model
                    )
                )
            ).use { response -> return CoreUtils.convertJsonDeviceState(response.body!!.string()); }
        } catch (exception: IOException) {
            throw GoveeWrappedException(exception)
        }
    }

    private fun sendCommand(json: String) {
        try {
            CoreUtils.put("/devices/control", json).use { response -> CoreUtils.checkResponse(response) }
        } catch (exception: IOException) {
            throw GoveeWrappedException(exception)
        }
    }
}