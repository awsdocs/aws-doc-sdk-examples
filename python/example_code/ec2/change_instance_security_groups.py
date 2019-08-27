# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[create_instance.py demonstrates how to create an Amazon EC2 instance.]
# snippet-service:[ec2]
# snippet-keyword:[Amazon Elastic Compute Cloud (Amazon EC2)]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-15]
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


def change_instance_security_groups(instance_id, security_group_ids):
    """Change the security groups assigned to an EC2 instance

    This method assigns the security groups to each elastic network interface
    attached to the EC2 instance.

    :param instance_id: EC2 instance ID
    :param security_group_ids: list of security group IDs
    :return True if the security groups were assigned to each network interface
    in the EC2 instance. Otherwise, False.
    """

    # Retrieve the IDs of the network interfaces attached to the EC2 instance
    ec2_client = boto3.client('ec2')
    try:
        response = ec2_client.describe_instances(InstanceIds=[instance_id])
    except ClientError as e:
        logging.error(e)
        return False
    instance_info = response['Reservations'][0]['Instances'][0]

    # Assign the security groups to each network interface
    for network_interface in instance_info['NetworkInterfaces']:
        try:
            ec2_client.modify_network_interface_attribute(
                NetworkInterfaceId=network_interface['NetworkInterfaceId'],
                Groups=security_group_ids)
        except ClientError as e:
            logging.error(e)
            return False
    return True


def main():
    """Exercise change_instance_security_groups()"""

    # Assign these values before running the program
    ec2_instance_id = 'INSTANCE_ID'
    security_group_ids = [
        'SECURITY_GROUP_ID_1',
        'SECURITY_GROUP_ID_2',
    ]

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Assign the security groups to the EC2 instance
    if change_instance_security_groups(ec2_instance_id, security_group_ids):
        logging.info(f'Changed EC2 Instance {ec2_instance_id} Security Groups to:')
        for security_group in security_group_ids:
            logging.info(f'    ID: {security_group}')


if __name__ == '__main__':
    main()
