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


def copy_object(src_bucket_name, src_object_name, dest_bucket_name, dest_object_name=None):
    """Copy an Amazon S3 bucket object.

    :param src_bucket_name: string
    :param src_object_name: string
    :param dest_bucket_name: string. Must already exist.
    :param dest_object_name: string. If dest bucket/object exists, it is overwritten. Default: src_object_name
    :return: True if object was copied, otherwise False
    """

    # Construct source bucket/object parameter
    copy_source = {'Bucket': src_bucket_name, 'Key': src_object_name}
    if dest_object_name is None:
        dest_object_name = src_object_name

    s3 = boto3.client('s3')
    try:
        s3.copy_object(CopySource=copy_source, Bucket=dest_bucket_name, Key=dest_object_name)
    except Exception as err:
        # err.response['Error']['Code'] == 'AllAccessDisabled' (if bucket does not exist), 'NoSuchKey',
        # or 'InvalidRequest' (if destination bucket/object == source bucket/object)
        return False
    return True


def main():
    src_bucket_name = 'src-bucket-name'
    src_object_name = 'src-object-name'
    dest_bucket_name = 'dest-bucket-name'
    dest_object_name = 'dest-object-name'
    result = copy_object(src_bucket_name, src_object_name, dest_bucket_name, dest_object_name)

    if not result:
        print('ERROR: Could not copy bucket object')
    else:
        print('Copied {0}/{1} to {2}/{3}'.format(src_bucket_name, src_object_name, dest_bucket_name, dest_object_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[copy_object.py demonstrates how to copy an Amazon S3 bucket object.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-09]
# snippet-sourceauthor:[scalwas (AWS)]
