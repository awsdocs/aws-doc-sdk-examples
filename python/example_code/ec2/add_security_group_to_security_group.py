# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[add_security_group_to_security_group.py demonstrates how to add a security group to another security group.]
# snippet-service:[ec2]
# snippet-keyword:[Amazon Elastic Compute Cloud (Amazon EC2)]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-20]
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


def add_security_group_to_security_group(security_group_id,
                                         source_security_group_name,
                                         source_security_group_owner_id=''):
    """Add a security group to another security group

    Adding a security group to another security group allows traffic
    from EC2 instances associated with the added group.

    For example, assume the following scenario:
      * EC2 instance #1 uses security group #1
      * EC2 instance #2 uses security group #2
    If security group #2 is added to security group #1 then EC2 instance #1
    will accept traffic from EC2 instance #2.

    Traffic is allowed from the private IP addresses of the EC2 instances, not
    the public IP or Elastic IP addresses. All types of traffic are allowed
    (UDP, TCP, and ICMP).

    Note: Adding a security group does not add the group's rules to the
    modified group.

    If a different AWS account owns the added security group then the account
    ID must be specified in the source_security_group_owner_id argument.

    :param security_group_id: ID of the security group to be modified
    :param source_security_group_name: Name of the security group to add
    :param source_security_group_owner_id: AWS account ID that owns the added
    security group. This argument is required only if the modified and added
    security groups are in different AWS accounts.
    :return True if group was added. Otherwise, False.
    """

    # Add the security group
    ec2_client = boto3.client('ec2')
    try:
        ec2_client.authorize_security_group_ingress(
            GroupId=security_group_id,
            SourceSecurityGroupName=source_security_group_name,
            SourceSecurityGroupOwnerId=source_security_group_owner_id)
    except ClientError as e:
        logging.error(e)
        return False
    return True


def main():
    """Exercise add_security_group_to_security_group()"""

    # Assign these values before running the program
    security_group_id = 'MODIFIED_SECURITY_GROUP_ID'    # Note: Group ID
    security_group_name = 'ADDED_SECURITY_GROUP_NAME'   # Note: Group Name
    added_security_group_owner_id = ''                  # AWS account ID

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Add the security group to the other security group
    if add_security_group_to_security_group(security_group_id,
                                            security_group_name,
                                            added_security_group_owner_id):
        logging.info(f'Added security group {security_group_name} '
                     f'to security group {security_group_id}')


if __name__ == '__main__':
    main()
