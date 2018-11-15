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


def put_object(dest_bucket_name, dest_object_name, src_data):
    """Adds an object to an Amazon S3 bucket.

    :param dest_bucket_name: string
    :param dest_object_name: string
    :param src_data: bytes of data or string reference to file spec
    :return: True if src_data was added to dest_bucket/dest_object, otherwise False
    :raises FileNotFoundError, IOError: The src_data argument references a non-existent file spec
    :raises TypeError: The src_data argument is not of a supported type
    """

    # The src_data argument must be of type bytes or string
    # Construct Body= parameter
    if isinstance(src_data, bytes):
        object_data = src_data
    elif isinstance(src_data, str):
        object_data = open(src_data, 'rb')      # possible FileNotFoundError/IOError exception
    else:
        raise TypeError('Type of ' + str(type(src_data)) + ' for the argument \'src_data\' is not supported.')

    s3 = boto3.client('s3')
    try:
        s3.put_object(Bucket=dest_bucket_name, Key=dest_object_name, Body=object_data)
    except Exception as e:
        # err.response['Error']['Code'] == 'AllAccessDisabled' (if bucket does not exist), 'NoSuchKey',
        # or 'InvalidRequest' (if destination bucket/object == source bucket/object)
        return False
    finally:
        if isinstance(src_data, str):
            object_data.close()
    return True


def main():
    test_bucket_name = 'test-bucket-name'
    test_object_name = 'test-object-name'
    filename = 'C:\\path\\to\\file.ext'
    # Alternatively, specify object contents using bytes.
    #filename = b'This is the data to store in the S3 object.'
    try:
        result = put_object(test_bucket_name, test_object_name, filename)
    except (FileNotFoundError, IOError):
        print('ERROR: Could not find {}'.format(filename))
        exit(-1)
    except TypeError as e:
        print('ERROR: {}'.format(e))
        exit(-1)

    if not result:
        print('ERROR: Could not add object to bucket')
    else:
        print('Added {0} to {1}'.format(test_object_name, test_bucket_name))


if __name__ == '__main__':
    main()

# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_object.py demonstrates how to add an object into an Amazon S3 bucket.]
# snippet-keyword:[Python]
# snippet-keyword:[AWS SDK for Python (Boto3)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[Amazon S3]
# snippet-service:[s3]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2018-11-09]
# snippet-sourceauthor:[scalwas (AWS)]
