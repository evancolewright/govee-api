package pro.evanwright.govee

import pro.evanwright.govee.exception.GoveeAPIException
import pro.evanwright.govee.exception.GoveeWrappedException
import pro.evanwright.govee.model.GoveeDevice
import java.io.IOException
import kotlin.collections.HashSet

/**
 * Use [GoveeAPI.initialize] to start using the API.<br></br>
 * Are you lost? Check the [README](https://github.com/evancolewright/govee-api) to learn how to use the GoveeAPI.
 */
object GoveeAPI  {
    private var deviceCache: Set<GoveeDevice> = HashSet();
    var apiKey = ""
        private set

    /**
     * Retrieves a [GoveeDevice] by its MAC address
     * @param macAddress  The MAC address to search for
     */
    fun getDeviceFromMacAddress(macAddress: String): GoveeDevice? {
       return deviceCache.find { goveeDevice: GoveeDevice -> goveeDevice.macAddress == macAddress }
    }
    /**
     * Retrieves a [GoveeDevice] by its user-defined name
     * @param name  The name to search for
     */
    fun getDeviceFromName(name: String): GoveeDevice? {
        return deviceCache.find { goveeDevice: GoveeDevice -> goveeDevice.name == name }
    }

    @Throws(IOException::class, GoveeAPIException::class)
    private fun initializeDeviceCache() {
        val response = CoreUtils.checkResponse(CoreUtils.get("/devices"))
        deviceCache = CoreUtils.convertJsonDevices(response.body!!.string());
    }

    /**
     * Initializes the API.  You may also use this function to reinitialize
     * the internal device cache.
     * @throws  GoveeWrappedException  if an [IOException] occurs
     */
    fun initialize(key: String = "") {
        if (key.isNotEmpty())
             apiKey = key;
        try {
            initializeDeviceCache()
        } catch(exception: IOException) {
            throw GoveeWrappedException(exception)
        }
    }
}