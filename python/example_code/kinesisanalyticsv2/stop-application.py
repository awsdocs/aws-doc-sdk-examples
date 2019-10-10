# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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


import boto3

import sys

from botocore.exceptions import ClientError

arguments = len(sys.argv) - 1

if arguments < 1:
    print("You must supply an application name")

else:

    application_name = sys.argv[1]

    # Create kinesisanalyticsv2 client
    client = boto3.client('kinesisanalyticsv2')

    # Stop the Application
    try:
        client.stop_application(
            ApplicationName=application_name
        )
    except ClientError as e:
        print("Got the following error calling stop_application: {}".format(e))


# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[stop-application.py demonstrates how to stop a particular Kinesis Data Alanytics Application]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AWS KinesisAnalyticsV2]
# snippet-service:[kinesisanalyticsv2]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-10-04]
# snippet-sourceauthor:[nprajilesh]