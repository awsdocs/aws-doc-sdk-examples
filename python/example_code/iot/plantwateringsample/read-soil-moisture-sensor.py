#
# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.
#

# snippet-sourcedescription:[read-soil-moisture-sensor.py demonstrates how to simulate soil moisture readings by generating random data. You push that data into the related shadow in AWS IoT.]
# snippet-service:[iot]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS IoT]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-25]
# snippet-sourceauthor:[pccornel]
# snippet-start:[iot.python.read-soil-moisture-sensor.complete]

import RPi.GPIO as GPIO
import time

# Represents the GPIO21 pin. 
channel = 21

# Use the GPIO BCM pin numbering scheme.
GPIO.setmode(GPIO.BCM)

# Receive input signals through the pin.
GPIO.setup(channel, GPIO.IN)

# Infinite loop to keep this script running.
while True:
  # 'No water' = 1/True (sensor's microcontroller light is off).
  if GPIO.input(channel):
    print("No water detected")
  else:
    # 'Water' = 0/False (microcontroller light is on).
    print("Water detected!")

  # Wait 5 seconds before checking again.
  time.sleep(5)

# Clean things up if for any reason we get to this
# point before script stops.
GPIO.cleanup()

# snippet-end:[iot.python.read-soil-moisture-sensor.complete]
