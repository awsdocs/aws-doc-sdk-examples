# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License"). You
# may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# or in the "license" file accompanying this file. This file is
# distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
# ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.
# snippet-start:[deeplens.python.deeplens_kvi_api.intro]

import time
import os
import DeepLens_Kinesis_Video as dkv
from botocore.session import Session
import greengrasssdk

def greengrass_hello_world_run():
    # Create the green grass client so that we can send messages to IoT console
    client = greengrasssdk.client('iot-data')
    iot_topic = '$aws/things/{}/infer'.format(os.environ['AWS_IOT_THING_NAME'])

    # Stream configuration, name and retention
    # Note that the name will appear as deeplens-myStream
    stream_name = 'myStream'
    retention = 2 #hours

    # Amount of time to stream
    wait_time = 60 * 60 * 5 #seconds

    # Use the boto session API to grab credentials
    session = Session()
    creds = session.get_credentials()

    # Create producer and stream.
    producer = dkv.createProducer(creds.access_key, creds.secret_key, creds.token, "us-east-1")
    client.publish(topic=iot_topic, payload="Producer created")
    kvs_stream = producer.createStream(stream_name, retention)
    client.publish(topic=iot_topic, payload="Stream {} created".format(stream_name))

    # Start putting data into the KVS stream
    kvs_stream.start()
    client.publish(topic=iot_topic, payload="Stream started")
    time.sleep(wait_time)
    # Stop putting data into the KVS stream
    kvs_stream.stop()
    client.publish(topic=iot_topic, payload="Stream stopped")

# Execute the function above
greengrass_hello_world_run()


#snippet-end:[deeplens.python.deeplens_kvi_api.intro]


#snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
#snippet-sourcedescription:[deeplens_kinesis_integration.py demonstrates how to create an inference Lambda function on an AWS DeepLens model.]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Botocore)]
#snippet-keyword:[AWS Lambda]
#snippet-keyword:[AWS Kinesis]
#snippet-keyword:[AWS Greengrass SDK]
#snippet-keyword:[Code Sample]
#snippet-keyword:[AWS DeepLens]
#snippet-service:[deeplens]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2019-01-07]
#snippet-sourceauthor:[AWS]
