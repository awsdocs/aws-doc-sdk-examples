# AWS IoT Data Plane Specification

This is a draft specification for a Basics code example demonstrating key features of the **AWS IoT Data Plane** service. The scenario tells the story of a smart home temperature monitoring system: registering an IoT device (thing), managing its device shadows (classic and named), publishing MQTT messages with retained flags, retrieving retained messages, and cleaning up all resources. This example teaches developers the core IoT Data Plane operations for shadow management and MQTT messaging through the AWS SDK.

> **DRAFT** — This specification is auto-generated and should be reviewed for accuracy before implementation.

### Resources

The scenario requires the following prerequisite AWS resources:

- **IoT Thing** — An IoT thing must be registered in the AWS IoT registry before shadow operations can be performed. Since this is an IoT Control Plane resource (same overall AWS IoT service family), it will be created using the **AWS IoT Control Plane** client (`iot` client) directly in the setup method. The `CreateThing` API is used for setup and `DeleteThing` for teardown.

No CloudFormation stacks or external service resources are required.

### Relevant documentation

* [What is AWS IoT?](https://docs.aws.amazon.com/iot/latest/developerguide/what-is-aws-iot.html)
* [AWS IoT Device Shadow service](https://docs.aws.amazon.com/iot/latest/developerguide/iot-device-shadows.html)
* [Device Shadow REST API](https://docs.aws.amazon.com/iot/latest/developerguide/device-shadow-rest-api.html)
* [MQTT Protocol in AWS IoT Core](https://docs.aws.amazon.com/iot/latest/developerguide/mqtt.html)
* [AWS IoT Data Plane API Reference](https://docs.aws.amazon.com/iot/latest/apireference/API_Operations_AWS_IoT_Data_Plane.html)
* [MQTT Retained Messages in AWS IoT Core](https://docs.aws.amazon.com/iot/latest/developerguide/mqtt-retained-messages.html)
* [AWS IoT Core Pricing - Messaging](https://aws.amazon.com/iot-core/pricing/#Messaging)

### API Actions Used

The following AWS IoT Data Plane actions are demonstrated:

* [UpdateThingShadow](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_UpdateThingShadow.html) — Updates the shadow for a specified thing.
* [GetThingShadow](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_GetThingShadow.html) — Gets the shadow for a specified thing.
* [ListNamedShadowsForThing](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_ListNamedShadowsForThing.html) — Lists the shadows for a specified thing.
* [DeleteThingShadow](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_DeleteThingShadow.html) — Deletes the shadow for a specified thing.
* [Publish](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_Publish.html) — Publishes an MQTT message to a topic.
* [ListRetainedMessages](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_ListRetainedMessages.html) — Lists retained messages stored for the account.
* [GetRetainedMessage](https://docs.aws.amazon.com/iot/latest/apireference/API_iotdata_GetRetainedMessage.html) — Gets the details of a single retained message.

The following AWS IoT Control Plane actions are used for setup/teardown only:

* [CreateThing](https://docs.aws.amazon.com/iot/latest/apireference/API_CreateThing.html) — Creates a thing record in the IoT registry (setup).
* [DeleteThing](https://docs.aws.amazon.com/iot/latest/apireference/API_DeleteThing.html) — Deletes a thing from the IoT registry (cleanup).

---

## Hello IoT Data Plane

The Hello example is a standalone runnable program that verifies connectivity to the AWS IoT Data Plane service.

- Create an IoT Data Plane client (note: the client requires a custom endpoint, obtained via `iot:DescribeEndpoint` with `endpointType=iot:Data-ATS`).
- Create an IoT Control Plane client.
- Call `CreateThing` to register a temporary thing named `hello-iot-data-plane-thing`.
- Call **GetThingShadow** for the newly created thing.
  - Since no shadow has been created yet, this will throw a `ResourceNotFoundException`.
  - Catch the exception and print a friendly message: "No shadow exists yet for this thing — this is expected for a newly created device."
- Call **UpdateThingShadow** with a minimal reported state: `{"state":{"reported":{"status":"online"}}}`.
- Call **GetThingShadow** again to confirm the shadow was created.
  - Parse the JSON payload and print the shadow document.
- Clean up by calling `DeleteThingShadow` for the thing, then `DeleteThing` to remove the thing from the registry.
- Print a success message.

---

## Scenario

This scenario demonstrates a realistic IoT device shadow management and MQTT messaging workflow for a smart home temperature sensor. The workflow covers creating and managing both classic (unnamed) and named device shadows, publishing MQTT messages with retained flags, and retrieving retained messages.

### Setup

1. **Obtain the IoT Data-ATS endpoint** — Use the IoT Control Plane client to call `DescribeEndpoint` with `endpointType=iot:Data-ATS`. Store the returned endpoint address. Configure the IoT Data Plane client to use this endpoint.

2. **Create an IoT Thing** — Use the IoT Control Plane client (`iot`) to call `CreateThing` with a unique thing name (e.g., `temp-sensor-{timestamp}`).
   - Store the thing name and thing ARN for later use.
   - Print: "Created IoT thing: {thingName} (ARN: {thingArn})"
   - If `ResourceAlreadyExistsException` is thrown, use the existing thing.

### Step 1: Create the classic (unnamed) device shadow with initial state

Use **UpdateThingShadow** to create the classic shadow for the thing with an initial reported state representing a temperature sensor coming online.

- **thingName**: The thing created in setup.
- **payload**: A JSON shadow document:
  ```json
  {
    "state": {
      "reported": {
        "temperature": 22.5,
        "humidity": 45,
        "battery_pct": 100,
        "firmware_version": "1.0.0",
        "status": "online"
      }
    }
  }
  ```
- Print: "Created classic device shadow with initial sensor readings."
- Display the payload returned in the response.

### Step 2: Retrieve and display the classic shadow

Use **GetThingShadow** to read back the classic shadow.

- **thingName**: The thing created in setup.
- (No `shadowName` parameter — this retrieves the classic/unnamed shadow.)
- Parse the returned JSON payload.
- Print the full shadow document, highlighting the `reported` state, `metadata` (timestamps), and `version` number.
- Print: "Classic shadow retrieved. Current temperature: {temperature}C, humidity: {humidity}%"

### Step 3: Create a named shadow for sensor configuration

Use **UpdateThingShadow** to create a named shadow that stores configuration settings separately from the sensor readings.

- **thingName**: The thing created in setup.
- **shadowName**: `sensor-config`
- **payload**: A JSON shadow document with both reported and desired states:
  ```json
  {
    "state": {
      "desired": {
        "reporting_interval_sec": 30,
        "temperature_unit": "celsius",
        "alert_threshold_high": 35.0,
        "alert_threshold_low": 5.0
      },
      "reported": {
        "reporting_interval_sec": 60,
        "temperature_unit": "celsius",
        "alert_threshold_high": 40.0,
        "alert_threshold_low": 0.0
      }
    }
  }
  ```
- Print: "Created named shadow 'sensor-config' with desired and reported configuration."
- Note: Because `desired` and `reported` differ, the shadow response will include a `delta` section showing the differences.

### Step 4: List all named shadows for the thing

Use **ListNamedShadowsForThing** to enumerate all named shadows attached to the thing.

- **thingName**: The thing created in setup.
- **pageSize**: 25
- Parse the response and display the list of shadow names returned in the `results` array.
- Print: "Found {count} named shadow(s) for thing '{thingName}': {shadow_names}"
- Also display the response `timestamp`.

### Step 5: Retrieve and inspect the named shadow with delta

Use **GetThingShadow** to read the named shadow and inspect the delta between desired and reported states.

- **thingName**: The thing created in setup.
- **shadowName**: `sensor-config`
- Parse the returned JSON payload.
- Print the full shadow document.
- Highlight the `delta` section, which shows the configuration changes the device should apply.
- Print: "The delta shows {count} configuration changes pending for the device."

### Step 6: Publish an MQTT message with retain flag

Use **Publish** to send a temperature reading message to an MQTT topic with the retain flag set, simulating a device status broadcast.

- **topic**: `dt/sensors/{thingName}/temperature` (where `{thingName}` is the actual thing name)
- **qos**: 1 (at-least-once delivery)
- **retain**: true (retain this message so new subscribers get the latest reading)
- **payload**: A JSON message body:
  ```json
  {
    "thing_name": "{thingName}",
    "temperature": 23.1,
    "humidity": 44,
    "timestamp": "{ISO8601_timestamp}",
    "unit": "celsius"
  }
  ```
- Print: "Published retained MQTT message to topic 'dt/sensors/{thingName}/temperature'"

### Step 7: List retained messages

Use **ListRetainedMessages** to enumerate retained messages stored in the account.

- **maxResults**: 25
- Parse the response and iterate over the `retainedTopics` array.
- For each retained topic summary, display:
  - `topic`: The topic name
  - `payloadSize`: Size of the retained payload in bytes
  - `qos`: The QoS level
  - `lastModifiedTime`: When the message was last updated
- Print: "Found {count} retained message(s). Looking for our sensor topic..."
- Identify our topic in the list (if present).

### Step 8: Get the retained message details

Use **GetRetainedMessage** to retrieve the full payload of the retained message published in Step 6.

- **topic**: `dt/sensors/{thingName}/temperature`
- Parse the response:
  - `payload`: The message payload (binary/bytes — decode to string)
  - `topic`: The topic name
  - `qos`: The QoS level
  - `lastModifiedTime`: Epoch timestamp
- Print: "Retrieved retained message payload: {parsed_payload}"
- Display the decoded JSON content of the message.

### Step 9: Update the named shadow to simulate device applying configuration

Use **UpdateThingShadow** to update the named shadow, simulating the device applying the desired configuration changes and reporting back.

- **thingName**: The thing created in setup.
- **shadowName**: `sensor-config`
- **payload**: A JSON shadow document where reported now matches the previously desired state:
  ```json
  {
    "state": {
      "reported": {
        "reporting_interval_sec": 30,
        "temperature_unit": "celsius",
        "alert_threshold_high": 35.0,
        "alert_threshold_low": 5.0
      },
      "desired": null
    }
  }
  ```
- Setting `desired` to `null` clears the desired state since the device has applied all changes.
- Print: "Device has applied configuration changes. Delta cleared."
- Optionally call **GetThingShadow** for `sensor-config` to verify the delta is now empty.

### Step 10: Delete the named shadow

Use **DeleteThingShadow** to remove the named shadow.

- **thingName**: The thing created in setup.
- **shadowName**: `sensor-config`
- Parse the response payload (the deleted shadow state is returned in JSON format).
- Print: "Deleted named shadow 'sensor-config'. Returned state: {payload_summary}"

### Cleanup

Cleanup MUST run even if the scenario fails (use try/finally pattern):

1. **Delete the retained message** — Call **Publish** with the same topic (`dt/sensors/{thingName}/temperature`), an empty/null payload, and `retain=true`. Publishing an empty payload with retain=true deletes the retained message for that topic.
   - Print: "Deleted retained message for topic 'dt/sensors/{thingName}/temperature'"

2. **Delete the classic (unnamed) shadow** — Call **DeleteThingShadow** with just the thingName (no shadowName) to remove the classic shadow.
   - Print: "Deleted classic device shadow."
   - If `ResourceNotFoundException`, the shadow was already deleted — continue.

3. **Delete the named shadow (if not already deleted)** — Call **DeleteThingShadow** with `shadowName=sensor-config`.
   - If `ResourceNotFoundException`, the shadow was already deleted in Step 10 — continue.

4. **Delete the IoT Thing** — Use the IoT Control Plane client to call `DeleteThing` with the thing name.
   - Print: "Deleted IoT thing: {thingName}"
   - If `ResourceNotFoundException`, the thing was already deleted — continue.

5. Print: "All resources cleaned up successfully."

### Outcome

After running this scenario, the user will have learned how to:

- Create and manage classic (unnamed) and named device shadows using `UpdateThingShadow` and `GetThingShadow`.
- Understand the `desired`, `reported`, and `delta` sections of a shadow document.
- List named shadows for a thing using `ListNamedShadowsForThing`.
- Publish MQTT messages with the retain flag using `Publish`.
- Retrieve retained messages using `ListRetainedMessages` and `GetRetainedMessage`.
- Delete shadows using `DeleteThingShadow`.
- Properly clean up retained messages by publishing an empty payload with retain=true.
- Handle common IoT Data Plane errors such as `ResourceNotFoundException`.

---

## Errors

SDK code examples include basic exception handling for each action used. The table below describes the single most relevant exception that will be handled in the code for each action in the context of this scenario.

| Action | Exception | Handling |
|-|-|-|
| `UpdateThingShadow` | `InvalidRequestException` | Validate the shadow JSON payload format and notify the user of malformed shadow documents. |
| `GetThingShadow` | `ResourceNotFoundException` | Notify the user that no shadow exists for the specified thing. In the Hello example, this is expected for newly created things. |
| `ListNamedShadowsForThing` | `ResourceNotFoundException` | Notify the user that the specified thing does not exist in the IoT registry. |
| `DeleteThingShadow` | `ResourceNotFoundException` | Notify the user the shadow has already been deleted. During cleanup, log and continue. |
| `Publish` | `InvalidRequestException` | Validate the MQTT topic name and payload. Notify the user of invalid topic format or payload size. |
| `ListRetainedMessages` | `ThrottlingException` | Notify the user the request rate exceeds the limit. Implement retry with exponential backoff. |
| `GetRetainedMessage` | `ResourceNotFoundException` | Notify the user that no retained message exists for the specified topic. |
| `CreateThing` (setup) | `ResourceAlreadyExistsException` | If the thing already exists with the same configuration, use the existing thing and continue. |
| `DeleteThing` (cleanup) | `ResourceNotFoundException` | The thing was already deleted. Log and continue cleanup. |

---

## Metadata

| action / scenario | metadata file | metadata key |
|-|-|-|
| `GetThingShadow` | iot-data-plane_metadata.yaml | iot-data-plane_GetThingShadow |
| `UpdateThingShadow` | iot-data-plane_metadata.yaml | iot-data-plane_UpdateThingShadow |
| `DeleteThingShadow` | iot-data-plane_metadata.yaml | iot-data-plane_DeleteThingShadow |
| `ListNamedShadowsForThing` | iot-data-plane_metadata.yaml | iot-data-plane_ListNamedShadowsForThing |
| `Publish` | iot-data-plane_metadata.yaml | iot-data-plane_Publish |
| `ListRetainedMessages` | iot-data-plane_metadata.yaml | iot-data-plane_ListRetainedMessages |
| `GetRetainedMessage` | iot-data-plane_metadata.yaml | iot-data-plane_GetRetainedMessage |
| `IoT Data Plane Hello` | iot-data-plane_metadata.yaml | iot-data-plane_Hello |
| `IoT Data Plane Basics Scenario` | iot-data-plane_metadata.yaml | iot-data-plane_Scenario |
