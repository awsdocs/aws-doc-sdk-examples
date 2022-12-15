# Sample apps for the AWS IoT Device SDK v2 for Python

This folder contains additional samples for the [aws-iot-device-sdk-python-v2](https://github.com/aws/aws-iot-device-sdk-python-v2).

 - [MQTT5 Request/Response](#mqtt5-requestresponse)

## MQTT5 Request/Response
This sample uses the
[Message Broker](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html)
for AWS IoT to send and receive messages
through an MQTT5 connection.

MQTT5 introduces additional features and enhancements that improve the development experience with MQTT. You can read more about MQTT5 in the Python V2 SDK by checking out the [MQTT5 user guide](../documents/MQTT5.md). This sample demonstrates how to use the request/response pattern that is enabled by MQTT5.

WARNING: This sample subscribes to both the request and response topics, but this is only for demonstration purposes. Ideally, another device would subscribe to the request topic and publish to the response topic (once received) but for simplicity, this sample acts as both devices.

Note: MQTT5 support is currently in **developer preview**. We encourage feedback at all times, but feedback during the preview window is especially valuable in shaping the final product. During the preview period we may make backwards-incompatible changes to the public API, but in general, this is something we will try our best to avoid.

On startup, the device connects to [AWS IoT Core](https://docs.aws.amazon.com/iot/latest/developerguide/iot-message-broker.html), subscribes to a request and response topic, and begins publishing messages to the request topic. The device will then receive messages back from the message broker and publish messages, along with the correlation data, to the response topic set in the received message's properties when it was published to the request topic. Finally, the device will start to receive messages from the response topic. Status updates will be continually printed to the console.

Source: `samples/mqtt5_request_response.py`

Your AWS IoT Core Thing's [Policy](https://docs.aws.amazon.com/iot/latest/developerguide/iot-policies.html) must provide privileges for this sample to connect, subscribe, publish, and receive. Make sure your policy allows a client ID of `test-*` to connect or use `--client_id <client ID here>` to send the client ID your policy supports.

<details>
<summary>(see sample policy)</summary>
<pre>
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "iot:Publish",
        "iot:Receive"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/command/control/light-1/switch",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topic/command/control/light-1/status"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Subscribe"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/command/control/light-1/switch",
        "arn:aws:iot:<b>region</b>:<b>account</b>:topicfilter/command/control/light-1/status"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "iot:Connect"
      ],
      "Resource": [
        "arn:aws:iot:<b>region</b>:<b>account</b>:client/test-*"
      ]
    }
  ]
}
</pre>
</details>

Run the sample like this:
``` sh
# For Windows: replace 'python3' with 'python'
python3 mqtt5_request_response.py --endpoint <iot_core_data_endpoint> --ca_file <file> --cert <file> --key <file>
```

