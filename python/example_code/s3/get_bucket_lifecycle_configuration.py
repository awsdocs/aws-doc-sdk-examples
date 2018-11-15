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


def get_bucket_lifecycle_configuration(bucket_name):
    """Retrieve the lifecycle configuration of an Amazon S3 bucket.

    :param bucket_name: string
    :return: None if bucket has no lifecycle configuration, otherwise return list of config rules. If error, return -1.
    """

    s3 = boto3.client('s3')
    try:
        response = s3.get_bucket_lifecycle_configuration(Bucket=bucket_name)
    except Exception as e:
        if e.response['Error']['Code'] == 'NoSuchLifecycleConfiguration':
            return None
        else:
            # e.response['Error']['Code'] == 'NoSuchBucket', etc.
            return -1
    return response['Rules']


def main():
    test_bucket_name = 'test-bucket-name'
    lifecycle_config = get_bucket_lifecycle_configuration(test_bucket_name)

    if lifecycle_config == -1:
        print('ERROR: Could not retrieve lifecycle configuration for {}'.format(test_bucket_name))
    elif lifecycle_config is None:
        print('{} does not have a lifecycle configuration.'.format(test_bucket_name))
    else:
        print(lifecycle_config)


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[get_bucket_lifecycle_configuration.py demonstrates how to retrieve the lifecycle configuration of an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-13]
# snippet-sourceauthor:[scalwas (AWS)]
