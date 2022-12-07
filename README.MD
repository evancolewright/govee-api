# govee-api
A lightweight, Kotlin wrapper around the Govee Lighting Web API.

### Installing (Maven)
1. Clone the repository from GitHub.
```
git clone https://github.com/evancolewright/govee-api.git
```
2. Install to your local .m2.
```
mvn clean install
```
3. Add the following dependency to your project.
```
<dependency>
    <groupId>pro.evanwright</groupId>
    <artifactId>govee-api</artifactId>
    <version>VERSION</version>
</dependency>
```

### Example
```kotlin
fun main(args: Array<String>) {
    // Initialize the API with your key
    GoveeAPI.initialize("d05200c6-a40b-428e-a75e-194bda311d0b")

    // Get the device and perform functions
    val device: GoveeDevice = GoveeAPI.getDeviceFromName("Desk lamp") ?: return
    device.turnOn()
    device.setColor("#74B72E")
    device.setBrightness(50)

    // Get the state of a device
    val deviceState: GoveeDeviceState = device.getDeviceState()
    print(deviceState.toString())
}
```

### Notes
- The API works by fetching all devices on your account when you call GoveeAPI#initialize.  If you add new devices, you will need to call it again.
- Most API-related functions throw some sort of GoveeAPIException.  You can wrap it with a try/catch to handle it.
- Unfortunately, The Govee Web API has a rather harsh rate limit.  Look out for a GoveeRateLimitException when your limit has been exceeded.

### License
MIT
