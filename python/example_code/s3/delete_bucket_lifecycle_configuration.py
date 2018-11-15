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


def delete_bucket_lifecycle_configuration(bucket_name):
    """Delete the lifecycle configuration of an Amazon S3 bucket.

    :param bucket_name: string
    :return: True if bucket lifecycle configuration was deleted, otherwise False.
    """

    s3 = boto3.client('s3')
    try:
        s3.delete_bucket_lifecycle(Bucket=bucket_name)
    except Exception as e:
        # e.response['Error']['Code'] == 'AllAccessDisabled' (bucket does not exist), etc.
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    result = delete_bucket_lifecycle_configuration(test_bucket_name)

    if not result:
        print('ERROR: Could not delete lifecycle configuration for {}'.format(test_bucket_name))
    else:
        print('Deleted the lifecycle configuration of {}'.format(test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_bucket_lifecycle_configuration.py demonstrates how to delete the lifecycle configuration of an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-13]
# snippet-sourceauthor:[scalwas (AWS)]
