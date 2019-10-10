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
# snippet-start:[eks.python.list_clusters.complete]


import boto3


def list_clusters(max_clusters=10, iter_marker=''):
    """List the Amazon EKS clusters in the AWS account's default region.

    :param max_clusters: Maximum number of clusters to retrieve.
    :param iter_marker: Marker used to identify start of next batch of clusters to retrieve
    :return: List of cluster names
    :return: String marking the start of next batch of clusters to retrieve. Pass this string as the iter_marker
        argument in the next invocation of list_clusters().
    """

    eks = boto3.client('eks')

    clusters = eks.list_clusters(maxResults=max_clusters, nextToken=iter_marker)
    marker = clusters.get('nextToken')       # None if no more clusters to retrieve
    return clusters['clusters'], marker


def main():
    clusters, marker = list_clusters()
    if not clusters:
        print('No clusters exist.')
    else:
        while True:
            # Print cluster names
            for cluster in clusters:
                print(cluster)

            # If no more clusters exist, exit loop, otherwise retrieve the next batch
            if marker is None:
                break
            clusters, marker = list_clusters(iter_marker=marker)


if __name__ == '__main__':
    main()

# snippet-end:[eks.python.list_clusters.complete]
# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_clusters.py demonstrates how to list the Amazon EKS clusters in the AWS account's default region.]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon Elastic Container Service for Kubernetes (Amazon EKS)]
# snippet-service:[eks]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-15]
# snippet-sourceauthor:[scalwas (AWS)]
