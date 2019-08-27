# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
#
# http://aws.amazon.com/apache2.0/
#
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[create_cluster_subnet_group.py demonstrates how to create a subnet group for an Amazon Redshift cluster.]
# snippet-service:[redshift]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Redshift]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-25]
# snippet-sourceauthor:[AWS]
# snippet-start:[redshift.python.create_cluster_subnet_group.complete]

import boto3
from botocore.exceptions import ClientError


def create_redshift_cluster_subnet_group(group_name,
                                         subnet_ids,
                                         Desc='Redshift cluster subnet group'):
    """Create a subnet group for an Amazon Redshift cluster

    :param group_name: string; Name to assign to the group
    :param subnet_ids: list of strings; List of existing subnet IDs
    :param Desc: string; Description of group
    :return: dictionary containing subnet information, otherwise None
    """

    redshift_client = boto3.client('redshift')
    try:
        response = redshift_client.create_cluster_subnet_group(
            ClusterSubnetGroupName=group_name,
            SubnetIds=subnet_ids,
            Description=Desc)
    except ClientError as e:
        print(f'ERROR: {e}')
        return None
    else:
        return response['ClusterSubnetGroup']
# snippet-end:[redshift.python.create_cluster_subnet_group.complete]


def main():
    """Test create_redshift_cluster_subnet_group()"""

    subnet_group_name = 'myTestRedshiftSubnetGroup'
    subnet_ids = ['subnet-1234abcd']  # Replace with an existing subnet ID
    description = 'Demo subnet group for Amazon Redshift'

    subnet_info = create_redshift_cluster_subnet_group(subnet_group_name,
                                                       subnet_ids,
                                                       description)
    if subnet_info is not None:
        print(f'Created cluster subnet group: {subnet_info["ClusterSubnetGroupName"]}')
        print(f'VPC ID: {subnet_info["VpcId"]}')
        print(f'Subnet group status: {subnet_info["SubnetGroupStatus"]}')
        for subnet in subnet_info['Subnets']:
            print(f'Subnet ID: {subnet["SubnetIdentifier"]}')
            print(f'    Availability Zone: {subnet["SubnetAvailabilityZone"]["Name"]}')
            print(f'    Status: {subnet["SubnetStatus"]}')


if __name__ == '__main__':
    main()
