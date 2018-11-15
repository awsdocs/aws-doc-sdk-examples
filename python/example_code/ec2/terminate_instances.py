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


def terminate_instances(instance_ids):
    """Terminate one or more Amazon EC2 instances

    :param instance_ids: List of EC2 instance IDs
    :return: List of state information for each instance specified in instance_ids. If error, return None.
    """

    ec2 = boto3.client('ec2')

    # Terminate each instance in the argument list
    try:
        states = ec2.terminate_instances(InstanceIds=instance_ids)
    except Exception as e:
        # e.response['Error']['Code'] == 'InvalidInstanceID.NotFound', etc.
        return None
    return states['TerminatingInstances']


def main():
    ec2 = boto3.resource('ec2')

    # Construct list of non-terminated instance IDs
    instance_ids = [instance.id for instance in ec2.instances.all() if instance.state['Name'] != 'terminated']

    if instance_ids:
        states = terminate_instances(instance_ids)

        if states is None:
            print("ERROR: Could not terminate all EC2 instances.")
        else:
            for state in states:
                print('ID: {}'.format(state['InstanceId']))
                print('  Current state: Code {0}, {1}'.format(state['CurrentState']['Code'],
                                                              state['CurrentState']['Name']))
                print('  Previous state: Code {0}, {1}'.format(state['PreviousState']['Code'],
                                                               state['PreviousState']['Name']))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[terminate_instances.py demonstrates how to terminate Amazon EC2 instances.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon EC2]
# snippet-service:[ec2]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-14]
# snippet-sourceauthor:[scalwas (AWS)]
