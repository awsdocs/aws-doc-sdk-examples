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


def bucket_exists(bucket_name):
    """Determine whether bucket_name exists and the user has permission to access it.

    :param bucket_name: string
    :return: True if the referenced bucket_name exists, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        response = s3.head_bucket(Bucket=bucket_name)
    except Exception as err:
        # err.response['Error']['Code'] == 404 (not found) or 404 (access denied)
        return False

    # response['ResponseMetadata']['HTTPStatusCode'] == 200
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    if bucket_exists(test_bucket_name):
        print('{} exists and you have permission to access it.'.format(test_bucket_name))
    else:
        print('{} does not exist or you do not have permission to access it.'.format(test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[bucket_exists.py demonstrates how to terminate whether an Amazon S3 bucket exists.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-07]
# snippet-sourceauthor:[scalwas (AWS)]
