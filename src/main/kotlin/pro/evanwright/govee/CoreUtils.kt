package pro.evanwright.govee

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import pro.evanwright.govee.exception.*
import pro.evanwright.govee.model.GoveeDevice
import pro.evanwright.govee.model.GoveeDeviceState
import java.awt.Color
import java.io.IOException
import java.util.HashSet

object CoreUtils {

    // ===================================
    // HTTP Utils
    // ===================================

    private val httpClient = OkHttpClient();
    private const val BASE_URL = "https://developer-api.govee.com/v1";

    private fun getBaseRequest(suffix: String): Request.Builder {
        return Request.Builder().header("Govee-API-Key", "d05200c6-a40b-428e-a75e-194bda311d0b").url(BASE_URL + suffix)
    }

    @Throws(IOException::class)
    fun get(url: String): Response {
        return httpClient.newCall(getBaseRequest(url).build()).execute()
    }

    @Throws(IOException::class)
    fun put(url: String, jsonBody: String): Response {
        return httpClient.newCall(
            getBaseRequest(url).put(jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull()))
                .build()
        ).execute()
    }

    @Throws(IOException::class)
    fun checkResponse(response: Response): Response {
        if (response.isSuccessful) return response;
        if (response.code == 429) throw GoveeRateLimitException("Rate limit exceeded.")

        val errorMessage: String = parseMessage(response.body!!.string())
        when (response.code) {
            403, 401 -> throw GoveeAuthorizationException(errorMessage)
            500 -> throw GoveeInternalException(errorMessage)
            400 -> throw GoveeInvalidParameterException(errorMessage)
            else -> throw GoveeUnknownException(errorMessage)
        }
    }


    // ===================================
    // JSON Utils
    // ===================================

    private fun parseMessage(json: String): String {
        return JSONObject(json).getString("message")
    }

    fun convertJsonDevices(json: String): Set<GoveeDevice> {
        val returnDevices: MutableSet<GoveeDevice> = HashSet()
        val dataJson = JSONObject(json).getJSONObject("data")
        val devicesJson = dataJson.getJSONArray("devices")

        for (i in 0 until devicesJson.length()) {
            val device = devicesJson.getJSONObject(i)
            val supportedCommands: MutableList<String> = ArrayList()
            val supportedCommandsJson = device.getJSONArray("supportCmds")
            for (j in 0 until supportedCommandsJson.length()) supportedCommands.add(supportedCommandsJson[i].toString())
            val colorTemp = device.getJSONObject("properties").getJSONObject("colorTem").getJSONObject("range")
            returnDevices.add(
                GoveeDevice(
                    device.getString("deviceName"),
                    device.getString("model"),
                    device.getString("device"),
                    device.getBoolean("controllable"),
                    device.getBoolean("retrievable"),
                    supportedCommands,
                    colorTemp.getInt("min"),
                    colorTemp.getInt("max")
                )
            )
        }
        return returnDevices
    }

    fun convertJsonDeviceState(json: String): GoveeDeviceState {
        val properties = JSONObject(json).getJSONObject("data").getJSONArray("properties")
        val color = properties.getJSONObject(3).getJSONObject("color")
        return GoveeDeviceState(
            properties.getJSONObject(0).getBoolean("online"),
            properties.getJSONObject(1).getString("powerState"),
            properties.getJSONObject(2).getInt("brightness"),
            Color(color.getInt("r"), color.getInt("g"), color.getInt("b"))
        )
    }

    fun createSimpleCommandJson(device: GoveeDevice, commandName: String?, commandValue: Any?): String {
        val requestBody = JSONObject()
        requestBody.put("device", device.macAddress)
        requestBody.put("model", device.model)
        val command = JSONObject()
        command.put("name", commandName)
        command.put("value", commandValue)
        requestBody.put("cmd", command)
        return requestBody.toString()
    }

    fun createColorCommandJson(device: GoveeDevice, color: Color): String {
        val requestBody = JSONObject()
        requestBody.put("device", device.macAddress)
        requestBody.put("model", device.model)
        val command = JSONObject()
        command.put("name", "color")
        val rgb = JSONObject()
        rgb.put("r", color.red)
        rgb.put("g", color.green)
        rgb.put("b", color.blue)
        command.put("value", rgb)
        requestBody.put("cmd", command)
        return requestBody.toString()
    }
}