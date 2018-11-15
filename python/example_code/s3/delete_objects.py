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


def delete_objects(bucket_name, object_names):
    """Delete multiple objects from an Amazon S3 bucket.

    :param bucket_name: string
    :param object_names: list of strings
    :return: True if the referenced objects were deleted, otherwise False
    """

    # Convert list of object names to appropriate data format
    objlist = [{'Key': obj} for obj in object_names]

    s3 = boto3.client('s3')
    try:
        s3.delete_objects(Bucket=bucket_name, Delete={'Objects': objlist})
    except Exception as err:
        # err.response['Error']['Code'] == 'NoSuchBucket', etc.
        return False
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_names = ['test01_object_name', 'test02_object_name']

    if delete_objects(test_bucket_name, test_object_names):
        print('All objects were deleted.')
    else:
        print('ERROR: All objects were not deleted.')


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_objects.py demonstrates how to delete multiple objects from an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-07]
# snippet-sourceauthor:[scalwas (AWS)]
