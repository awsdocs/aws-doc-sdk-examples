# Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

def get_console_output(instance_id):
    """
    Using EC2 GetConsoleOutput API according
        https://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_GetConsoleOutput.html
    """
    ec2 = boto3.resource('ec2')
    ec2_instance = ec2.Instance(instance_id)
    json_output = ec2_instance.console_output()

    return json_output.get('Output', '')

def main():
    if len(sys.argv) == 1:
        print("Usage: {0} <instance-id>".format(sys.argv[0]))
        sys.exit(1)

    instance_id = sys.argv[1]
    output = get_console_output(instance_id)
    print(output)

    return 0

if __name__ == '__main__':
    sys.exit(main())
 

#snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
#snippet-keyword:[Python]
#snippet-keyword:[AWS SDK for Python (Boto3)]
#snippet-keyword:[Code Sample]
#snippet-keyword:[Amazon EC2]
#snippet-service:[ec2]
#snippet-sourcetype:[full-example]
#snippet-sourcedate:[2018-06-25]
#snippet-sourceauthor:[jschwarzwalder]

