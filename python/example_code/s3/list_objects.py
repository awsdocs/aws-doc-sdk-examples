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


def list_bucket_objects(bucket_name):
    """List the objects in an Amazon S3 bucket.

    :param bucket_name: string
    :return: List of bucket objects. If error, return None.
    """

    s3 = boto3.client('s3')
    try:
        response = s3.list_objects_v2(Bucket=bucket_name)
    except Exception as err:
        # err.response['Error']['Code'] == 'NoSuchBucket', etc.
        return None
    return response['Contents']


def main():
    test_bucket_name = 'test-bucket-name'
    objects = list_bucket_objects(test_bucket_name)

    if objects is None:
        print('ERROR: Could not retrieve objects')
    else:
        # List the object names
        print('Objects in {}'.format(test_bucket_name))
        for obj in objects:
            print('  {}'.format(obj['Key']))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_objects.py demonstrates how to list the objects in an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-08]
# snippet-sourceauthor:[scalwas (AWS)]
