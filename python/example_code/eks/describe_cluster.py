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
# snippet-start:[eks.python.describe_clusters.complete]

import boto3


def describe_cluster(cluster_name):
    """Retrieve information about an Amazon EKS cluster

    :param cluster_name: string
    :return: Dictionary containing information of cluster. If error, return None.
    """

    eks = boto3.client('eks')

    try:
        response = eks.describe_cluster(name=cluster_name)
    except Exception as e:
        # e.response['Error']['Code'] == 'ResourceNotFoundException'
        return None
    return response['cluster']


def main():
    test_cluster_name = 'test-cluster-name'

    result = describe_cluster(test_cluster_name)
    if result is None:
        print('ERROR: Could not retrieve information about cluster {}'.format(test_cluster_name))
    else:
        print('Cluster Name: {}'.format(result['name']))
        print('Status: {}'.format(result['status']))
        # Some information is not available until after the cluster has been created
        if result['status'] != 'CREATING':
            print('ARN: {}'.format(result['arn']))
            print('Endpoint: {}'.format(result['endpoint']))
            print('Certificate Authority (truncated): {}...'.format(result['certificateAuthority']['data'][:40]))


if __name__ == '__main__':
    main()

# snippet-end:[eks.python.describe_clusters.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[describe_clusters.py demonstrates how to retrieve information about an Amazon EKS cluster.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Elastic Container Service for Kubernetes (Amazon EKS)]
# snippet-service:[eks]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-15]
# snippet-sourceauthor:[scalwas (AWS)]

