# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[terminate_instances.py demonstrates how to terminate an Amazon EC2 instance.]
# snippet-service:[ec2]
# snippet-keyword:[Amazon EC2]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-11]
# snippet-sourceauthor:[AWS]

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

import logging
import boto3
from botocore.exceptions import ClientError


def terminate_instances(instance_ids):
    """Terminate one or more Amazon EC2 instances

    :param instance_ids: List of string IDs of EC2 instances to terminate
    :return: List of state information for each instance specified in instance_ids.
            If error, return None.
    """


    # Terminate each instance in the argument list
    ec2 = boto3.client('ec2')
    try:
        states = ec2.terminate_instances(InstanceIds=instance_ids)
    except ClientError as e:
        logging.error(e)
        return None
    return states['TerminatingInstances']


def main():
    """Exercise terminate_instances()"""

    # Assign these values before running the program
    ec2_instance_ids = ['EC2_INSTANCE_ID']

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Terminate the EC2 instance(s)
    states = terminate_instances(ec2_instance_ids)
    if states is not None:
        logging.debug('Terminating the following EC2 instances')
        for state in states:
            logging.debug(f'ID: {state["InstanceId"]}')
            logging.debug(f'  Current state: Code {state["CurrentState"]["Code"]}, '
                          f'{state["CurrentState"]["Name"]}')
            logging.debug(f'  Previous state: Code {state["PreviousState"]["Code"]}, '
                          f'{state["PreviousState"]["Name"]}')


if __name__ == '__main__':
    main()
