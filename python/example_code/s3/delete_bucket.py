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


def delete_bucket(bucket_name):
    """Delete an empty S3 bucket. If the bucket is not empty, the operation fails.

    :param bucket_name: string
    :return: True if the referenced bucket was deleted, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        response = s3.delete_bucket(Bucket=bucket_name)
    except Exception as err:
        # err.response['Error']['Code'] == 'BucketNotEmpty' or (if bucket does not exist) 'AllAccessDisabled'
        return False

    # response['ResponseMetadata']['HTTPStatusCode'] == 204 (No Content)
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    if delete_bucket(test_bucket_name):
        print('{} was deleted.'.format(test_bucket_name))
    else:
        print('ERROR: {} was not deleted.'.format(test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_bucket.py demonstrates how to delete an empty Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-07]
# snippet-sourceauthor:[scalwas (AWS)]
