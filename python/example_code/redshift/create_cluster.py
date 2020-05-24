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

# snippet-sourcedescription:[create_cluster.py demonstrates how to create an Amazon Redshift cluster.]
# snippet-service:[redshift]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Amazon Redshift]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-28]
# snippet-sourceauthor:[AWS]
# snippet-start:[redshift.python.create_cluster.complete]

import boto3
from botocore.exceptions import ClientError


def create_redshift_cluster(ClusterId):
    """Create an Amazon Redshift cluster

    The function returns without waiting for the cluster to be fully created.

    :param ClusterId: string; Name to assign to the cluster
    :return: dictionary containing cluster information, otherwise None.
    """

    redshift_client = boto3.client('redshift')
    try:
        # Modify argument values as necessary
        response = redshift_client.create_cluster(
            ClusterIdentifier=ClusterId,
            NodeType='dc2.large',
            MasterUsername='awsuser',
            MasterUserPassword='AWSuser_01',
            ClusterSubnetGroupName='myredshiftsubnetgroup',
            NumberOfNodes=2,
            IamRoles=['arn:aws:iam::123456789012:role/myRedshiftRole'],)
    except ClientError as e:
        print(f'ERROR: {e}')
        return None
    else:
        return response['Cluster']
# snippet-end:[redshift.python.create_cluster.complete]


def main():
    """Test create_redshift_cluster()"""

    cluster_identifier = 'redshift-cluster-1'

    cluster_info = create_redshift_cluster(cluster_identifier)
    if cluster_info is not None:
        print(f'Creating cluster: {cluster_info["ClusterIdentifier"]}')
        print(f'Cluster status: {cluster_info["ClusterStatus"]}')
        print(f'Database name: {cluster_info["DBName"]}')


if __name__ == '__main__':
    main()
