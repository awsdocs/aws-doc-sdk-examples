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


def delete_object(bucket_name, object_name):
    """Delete an object from an S3 bucket.

    :param bucket_name: string
    :param object_name: string
    :return: True if the referenced object was deleted, otherwise False
    """

    s3 = boto3.client('s3')
    try:
        response = s3.delete_object(Bucket=bucket_name, Key=object_name)
    except Exception as err:
        # err.response['Error']['Code'] == 'NoSuchBucket', etc.
        return False

    # response['ResponseMetadata']['HTTPStatusCode'] == 204 (No Content)
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_name = 'test-object-name'

    if delete_object(test_bucket_name, test_object_name):
        print('{} was deleted.'.format(test_object_name))
    else:
        print('ERROR: {} was not deleted.'.format(test_object_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_object.py demonstrates how to delete an object from an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-07]
# snippet-sourceauthor:[scalwas (AWS)]
