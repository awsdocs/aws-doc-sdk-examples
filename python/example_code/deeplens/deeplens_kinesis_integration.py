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
# snippet-start:[deeplens.python.deeplens_kinesis_integration.test_install]

import DeepLens_Kinesis_Video as dkv
import time

aws_access_key = "Your IAM access key"
aws_secrete_key = "Your IAM secret key"
region = "us-east-1"
stream_name ="Your stream name, for example, deeplens-kvs"
retention = 1 #Value in minutes
wait_time_sec = 60*2 #Number of minutes to stream the data
producer = dkv.createProducer(aws_access_key, aws_secrete_key, "", region) #No session token needed
my_stream = producer.createStream(stream_name, retention)
my_stream.start()
time.sleep(wait_time_sec)
my_stream.stop()

# snippet-end:[deeplens.python.deeplens_kinesis_integration.test_install]


# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[deeplens_kinesis_integration.py demonstrates how to create an inference Lambda function on an AWS DeepLens model.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS Kinesis]
# snippet-keyword:[AWS Lambda]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS DeepLens]
# snippet-service:[deeplens]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-07]
# snippet-sourceauthor:[AWS]
