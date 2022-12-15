# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0.

import command_line_utils
from awscrt import mqtt5, exceptions
from uuid import uuid4
import threading
from concurrent.futures import Future
import time, json

TIMEOUT = 100
request_topic_filter = "command/control/light-1/switch"
response_topic_filter = "command/control/light-1/status"
light_status = "OFF"


# Parse arguments
cmdUtils = command_line_utils.CommandLineUtils("PubSub - Send and receive messages through an MQTT5 connection.")
cmdUtils.add_common_mqtt5_commands()
cmdUtils.add_common_topic_message_commands()
cmdUtils.add_common_proxy_commands()
cmdUtils.add_common_logging_commands()
cmdUtils.register_command("key", "<path>", "Path to your key in PEM format.", True, str)
cmdUtils.register_command("cert", "<path>", "Path to your client certificate in PEM format.", True, str)
cmdUtils.register_command(
    "port",
    "<int>",
    "Connection port. AWS IoT supports 433 and 8883 (optional, default=auto).",
    type=int)
cmdUtils.register_command(
    "client_id",
    "<str>",
    "Client ID to use for MQTT5 connection (optional, default=None).",
    default="test-" + str(uuid4()))
cmdUtils.register_command(
    "count",
    "<int>",
    "The number of messages to send (optional, default='10').",
    default=10,
    type=int)
cmdUtils.register_command("is_ci", "<str>", "If present the sample will run in CI mode (optional, default='None')")
# Needs to be called so the command utils parse the commands
cmdUtils.get_args()

response_count = 0
received_all_event = threading.Event()
future_stopped = Future()
future_connection_success = Future()
is_ci = cmdUtils.get_command("is_ci", None) != None

# Callback when any publish is received
def on_publish_received(publish_packet_data):
    publish_packet = publish_packet_data.publish_packet
    assert isinstance(publish_packet, mqtt5.PublishPacket)

    global response_count
    response_count += 1

    if publish_packet.topic == request_topic_filter:

        print("Received request from {} with correlation data {}".format(publish_packet.topic, publish_packet.correlation_data))

        global light_status
        if light_status == "ON":
            light_status = "OFF"
        else:
            light_status = "ON"

        print("Publishing light status {} to response topic '{}'\n".format(light_status, publish_packet.response_topic))

        client.publish(mqtt5.PublishPacket(
            topic=publish_packet.response_topic,
            qos=mqtt5.QoS.AT_LEAST_ONCE,
            correlation_data=publish_packet.correlation_data,
            payload=light_status
        ))
    elif publish_packet.topic == response_topic_filter:
        print("Received light status {} from response topic {} with correlation data {}\n".format(str(publish_packet.payload), publish_packet.topic, publish_packet.correlation_data))

    if response_count == cmdUtils.get_command("count"):
        received_all_event.set()

# Callback for the lifecycle event Stopped
def on_lifecycle_stopped(lifecycle_stopped_data: mqtt5.LifecycleStoppedData):
    print("Lifecycle Stopped")
    global future_stopped
    future_stopped.set_result(lifecycle_stopped_data)


# Callback for the lifecycle event Connection Success
def on_lifecycle_connection_success(lifecycle_connect_success_data: mqtt5.LifecycleConnectSuccessData):
    print("Lifecycle Connection Success")
    global future_connection_success
    future_connection_success.set_result(lifecycle_connect_success_data)


# Callback for the lifecycle event Connection Failure
def on_lifecycle_connection_failure(lifecycle_connection_failure: mqtt5.LifecycleConnectFailureData):
    print("Lifecycle Connection Failure")
    print("Connection failed with exception:{}".format(lifecycle_connection_failure.exception))


if __name__ == '__main__':
    print("\nStarting MQTT5 PubSub Sample\n")
    message_count = cmdUtils.get_command("count")

    client = cmdUtils.build_mqtt5_client(
        on_publish_received=on_publish_received,
        on_lifecycle_stopped=on_lifecycle_stopped,
        on_lifecycle_connection_success=on_lifecycle_connection_success,
        on_lifecycle_connection_failure=on_lifecycle_connection_failure)
    print("MQTT5 Client Created")

    if is_ci == False:
        print("Connecting to {} with client ID '{}'...".format(
            cmdUtils.get_command(cmdUtils.m_cmd_endpoint), cmdUtils.get_command("client_id")))
    else:
        print("Connecting to endpoint with client ID")

    client.start()

    lifecycle_connect_success_data = future_connection_success.result(TIMEOUT)
    connack_packet = lifecycle_connect_success_data.connack_packet
    negotiated_settings = lifecycle_connect_success_data.negotiated_settings
    if is_ci == False:
        print("Connected to endpoint:'{}' with Client ID:'{}' with reason_code:{}".format(
            cmdUtils.get_command(cmdUtils.m_cmd_endpoint),
            connack_packet.assigned_client_identifier,
            repr(connack_packet.reason_code)))

    # Subscribing to the response topic
    print("Subscribing to response topic '{}'...".format(response_topic_filter))
    subscribe_future = client.subscribe(subscribe_packet=mqtt5.SubscribePacket(
        subscriptions=[mqtt5.Subscription(
            topic_filter=response_topic_filter,
            qos=mqtt5.QoS.AT_LEAST_ONCE)]
    ))
    suback = subscribe_future.result(TIMEOUT)
    print("Subscribed to response topic with {} \n".format(suback.reason_codes))

    #Subcribing to the request topic is for sample purposes only. Ideally, this subcription would take place on another device.
    print("Subscribing to request topic '{}'...".format(request_topic_filter))
    subscribe_future = client.subscribe(subscribe_packet=mqtt5.SubscribePacket(
        subscriptions=[mqtt5.Subscription(
            topic_filter=request_topic_filter,
            qos=mqtt5.QoS.AT_LEAST_ONCE)]
    ))
    suback = subscribe_future.result(TIMEOUT)
    print("Subscribed to request topic  with {} \n".format(suback.reason_codes))

    # Publish message to server desired number of times.
    # This step loops forever if count was set to 0.
    if message_count == 0:
        print("Sending messages until program killed")
    else:
        print("Sending {} message(s)".format(message_count))

    publish_count = 1
    while (publish_count <= message_count) or (message_count == 0):
        correlation_data = {
            "light_id": 4,
            "correlation_id": str(uuid4())
            }
        print("Publishing request to request topic {} with correlation data {}".format(request_topic_filter, str(correlation_data)))
        publish_future = client.publish(mqtt5.PublishPacket(
            topic=request_topic_filter,
            qos=mqtt5.QoS.AT_LEAST_ONCE,
            correlation_data=json.dumps(correlation_data),
            response_topic=response_topic_filter
        ))

        publish_completion_data = publish_future.result(TIMEOUT)
        print("PubAck to request topic received with {}\n".format(repr(publish_completion_data.puback.reason_code)))

        time.sleep(1)
        publish_count += 1

    received_all_event.wait(TIMEOUT)
    print("{} Response(s) received.".format(response_count))

    # Unsubscribe from response topic 
    print("Unsubscribing from response topic '{}'".format(response_topic_filter))
    unsubscribe_future = client.unsubscribe(unsubscribe_packet=mqtt5.UnsubscribePacket(
        topic_filters=[response_topic_filter]))
    unsuback = unsubscribe_future.result(TIMEOUT)
    print("Unsubscribed with {}".format(unsuback.reason_codes))


    # Unsubscribe from request topic 
    print("Unsubscribing from request topic '{}'".format(request_topic_filter))
    unsubscribe_future = client.unsubscribe(unsubscribe_packet=mqtt5.UnsubscribePacket(
        topic_filters=[request_topic_filter]))
    unsuback = unsubscribe_future.result(TIMEOUT)
    print("Unsubscribed with {}".format(unsuback.reason_codes))

    print("Stopping Client")
    client.stop()

    future_stopped.result(TIMEOUT)
    print("Client Stopped!")
