# Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# 
# http://aws.amazon.com/apache2.0/
# 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[thing_performance.py demonstrates how to push CPU and memory usage data to a thing's device shadow in AWS IoT.]
# snippet-service:[iot]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Python]
# snippet-keyword:[AWS IoT]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWSIoTSDK]
# snippet-keyword:[AWSIoTMQTTShadowClient]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2020-01-23]
# snippet-sourceauthor:[FThompsonAWS]
# snippet-start:[iot.python.thing_performance.complete]

from AWSIoTPythonSDK.MQTTLib import AWSIoTMQTTShadowClient

import json
import psutil
import argparse
import logging
import time

def configureParser():
    parser = argparse.ArgumentParser()
    parser.add_argument("-e", "--endpoint", action="store", required=True, dest="host",
            help="Your AWS IoT custom endpoint")
    parser.add_argument("-r", "--rootCA", action="store", required=True, dest="rootCAPath", help="Root CA file path")
    parser.add_argument("-c", "--cert", action="store", required=True, dest="certificatePath",
            help="Certificate file path")
    parser.add_argument("-k", "--key", action="store", required=True, dest="privateKeyPath",
            help="Private key file path")
    parser.add_argument("-p", "--port", action="store", dest="port", type=int, default=8883,
            help="Port number override")
    parser.add_argument("-n", "--thingName", action="store", required=True, dest="thingName",
            help="Targeted thing name")
    parser.add_argument("-d", "--requestDelay", action="store", dest="requestDelay", type=float, default=1,
            help="Time between requests (in seconds)")
    parser.add_argument("-v", "--enableLogging", action="store_true", dest="enableLogging",
            help="Enable logging for the AWS IoT Device SDK for Python")
    return parser


# An MQTT shadow client that uploads device performance data to AWS IoT at a regular interval.
class PerformanceShadowClient:
    def __init__(self, thingName, host, port, rootCAPath, privateKeyPath, certificatePath, requestDelay):
        self.thingName = thingName
        self.host = host
        self.port = port
        self.rootCAPath = rootCAPath
        self.privateKeyPath = privateKeyPath
        self.certificatePath = certificatePath
        self.requestDelay = requestDelay

    # Updates this thing's shadow with system performance data at a regular interval.
    def run(self):
        print("Connecting MQTT client for {}...".format(self.thingName))
        mqttClient = self.configureMQTTClient()
        mqttClient.connect()
        print("MQTT client for {} connected".format(self.thingName))
        deviceShadowHandler = mqttClient.createShadowHandlerWithName(self.thingName, True)

        print("Running performance shadow client for {}...\n".format(self.thingName))
        while True:
            performance = self.readPerformance()
            print("[{}]".format(self.thingName))
            print("CPU:\t{}%".format(performance["cpu"]))
            print("Memory:\t{}%\n".format(performance["memory"]))
            payload = { "state": { "reported": performance } }
            deviceShadowHandler.shadowUpdate(json.dumps(payload), self.shadowUpdateCallback, 5)
            time.sleep(args.requestDelay)

    # Configures the MQTT shadow client for this thing.
    def configureMQTTClient(self):
        mqttClient = AWSIoTMQTTShadowClient(self.thingName)
        mqttClient.configureEndpoint(self.host, self.port)
        mqttClient.configureCredentials(self.rootCAPath, self.privateKeyPath, self.certificatePath)
        mqttClient.configureAutoReconnectBackoffTime(1, 32, 20)
        mqttClient.configureConnectDisconnectTimeout(10)
        mqttClient.configureMQTTOperationTimeout(5)
        return mqttClient

    # Returns the local device's CPU usage, memory usage, and timestamp.
    def readPerformance(self):
        cpu = psutil.cpu_percent()
        memory = psutil.virtual_memory().percent
        timestamp = time.time()
        return { "cpu": cpu, "memory": memory, "timestamp": timestamp }
    
    # Prints the result of a shadow update call.
    def shadowUpdateCallback(self, payload, responseStatus, token):
        print("[{}]".format(self.thingName))
        print("Update request {} {}\n".format(token, responseStatus))


# Configures debug logging for the AWS IoT Device SDK for Python.
def configureLogging():
    logger = logging.getLogger("AWSIoTPythonSDK.core")
    logger.setLevel(logging.DEBUG)
    streamHandler = logging.StreamHandler()
    formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
    streamHandler.setFormatter(formatter)
    logger.addHandler(streamHandler)


# Runs the performance shadow client with user arguments.
if __name__ == "__main__":
    parser = configureParser()
    args = parser.parse_args()
    if (args.enableLogging):
        configureLogging()
    thingClient = PerformanceShadowClient(args.thingName, args.host, args.port, args.rootCAPath, args.privateKeyPath,
            args.certificatePath, args.requestDelay)
    thingClient.run()

# snippet-end:[iot.python.thing_performance.complete]
