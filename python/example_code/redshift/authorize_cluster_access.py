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

# snippet-sourcedescription:[authorize_cluster_access.py demonstrates how to enable access to Amazon Redshift clusters.]
# snippet-service:[redshift]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Redshift]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-28]
# snippet-sourceauthor:[AWS]
# snippet-start:[redshift.python.authorize_cluster_access.complete]

import boto3
from botocore.exceptions import ClientError


def authorize_cluster_access(IpAddress='0.0.0.0/0'):
    """Enable access to Amazon Redshift clusters

    Defines a security group inbound rule for the default VPC. The rule
    enables access to Redshift clusters by IP addresses referenced in the
    IpAddress argument. To define the rule, EC2 permissions are required.

    :param IpAddress: string; IP addresses to authorize access to Redshift
    clusters. Default: '0.0.0.0/0' allows access from any computer, which is
    reasonable for demonstration purposes, but is not appropriate in a
    production environment.
    :return: True if cluster access is enabled, else False
    """

    ec2_client = boto3.client('ec2')

    # Redshift uses port 5439 by default. If Redshift was configured to use
    # a different port, specify the FromPort= and ToPort= arguments accordingly.
    try:
        ec2_client.authorize_security_group_ingress(GroupName='default',
                                                    IpProtocol='tcp',
                                                    FromPort=5439,
                                                    ToPort=5439,
                                                    CidrIp=IpAddress)
    except ClientError as e:
        print(f'ERROR: {e}')
        return False
    return True
# snippet-end:[redshift.python.authorize_cluster_access.complete]


def main():
    """Test authorize_cluster_access()"""
    if not authorize_cluster_access():
        print('FAIL: authorize_cluster_access()')
        exit(1)


if __name__ == '__main__':
    main()
