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

# Lifecycle configuration settings
# The optional ID can be any descriptive string.
# The empty Prefix setting causes all objects in the bucket to transition.
# The 0 Days setting causes the transition to occur "immediately" (or within a short time period, less than one day)
lifecycle_config_settings = {
    'Rules': [
        {'ID': 'Glacier Transition Rule',
         'Filter': {'Prefix': ''},
         'Status': 'Enabled',
         'Transitions': [
             {'Days': 0,
              'StorageClass': 'GLACIER'}
         ]}
    ]}


def put_bucket_lifecycle_configuration(bucket_name, lifecycle_config):
    """Set the lifecycle configuration of an Amazon S3 bucket.

    :param bucket_name: string
    :param lifecycle_config: dict of lifecycle configuration settings
    :return: True if lifecycle configuration was set, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        s3.put_bucket_lifecycle_configuration(Bucket=bucket_name, LifecycleConfiguration=lifecycle_config)
    except Exception as e:
        # e.response['Error']['Code'] == 'NoSuchBucket' if bucket does not exist
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'

    # Set the bucket's lifecycle configuration
    result = put_bucket_lifecycle_configuration(test_bucket_name, lifecycle_config_settings)

    if not result:
        print('ERROR: Could not set the lifecycle configuration for {}'.format(test_bucket_name))
    else:
        print('The lifecycle configuration was set for {}'.format(test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_bucket_lifecycle_configuration.py demonstrates how to set the lifecycle configuration of an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-13]
# snippet-sourceauthor:[scalwas (AWS)]
