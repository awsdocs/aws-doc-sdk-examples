# Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
#
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of the
# License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
# OF ANY KIND, either express or implied. See the License for the specific
# language governing permissions and limitations under the License.

import sys
import boto3


ec2 = boto3.client('ec2')
if sys.argv[2] == 'ON':
    response = ec2.monitor_instances(InstanceIds=['INSTANCE_ID'])
else:
    response = ec2.unmonitor_instances(InstanceIds=['INSTANCE_ID'])
print(response)
