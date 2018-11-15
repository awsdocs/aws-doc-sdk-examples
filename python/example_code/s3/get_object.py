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


def get_object(bucket_name, object_name):
    """Retrieve an object from an Amazon S3 bucket.

    :param bucket_name: string
    :param object_name: string
    :return: botocore.response.StreamingBody object. If error, return None.
    """

    s3 = boto3.client('s3')
    try:
        response = s3.get_object(Bucket=bucket_name, Key=object_name)
    except Exception as err:
        # err.response['Error']['Code'] == 'NoSuchBucket' or 'NoSuchKey'
        return None
    # Return an open StreamingBody object
    return response['Body']


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_name = 'test-object-name'
    stream = get_object(test_bucket_name, test_object_name)

    if stream is None:
        print('ERROR: Could not retrieve bucket object')
    else:
        # Read entire object as bytes into memory
        data = stream.read()

        # Output object's beginning characters
        print('{0}: {1} ...'.format(test_object_name, data[:60]))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[get_object.py demonstrates how to retrieve an object from an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-09]
# snippet-sourceauthor:[scalwas (AWS)]
