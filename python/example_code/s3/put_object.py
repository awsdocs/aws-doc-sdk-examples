# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[put_object.py demonstrates how to add an object into an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-15]
# snippet-sourceauthor:[AWS]

# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import logging
import boto3
from botocore.exceptions import ClientError


def put_object(dest_bucket_name, dest_object_name, src_data):
    """Add an object to an Amazon S3 bucket

    The src_data argument must be of type bytes or a string that references
    a file specification.

    :param dest_bucket_name: string
    :param dest_object_name: string
    :param src_data: bytes of data or string reference to file spec
    :return: True if src_data was added to dest_bucket/dest_object, otherwise
    False
    """

    # Construct Body= parameter
    if isinstance(src_data, bytes):
        object_data = src_data
    elif isinstance(src_data, str):
        try:
            object_data = open(src_data, 'rb')
            # possible FileNotFoundError/IOError exception
        except Exception as e:
            logging.error(e)
            return False
    else:
        logging.error('Type of ' + str(type(src_data)) +
                      ' for the argument \'src_data\' is not supported.')
        return False

    # Put the object
    s3 = boto3.client('s3')
    try:
        s3.put_object(Bucket=dest_bucket_name, Key=dest_object_name, Body=object_data)
    except ClientError as e:
        # AllAccessDisabled error == bucket not found
        # NoSuchKey or InvalidRequest error == (dest bucket/obj == src bucket/obj)
        logging.error(e)
        return False
    finally:
        if isinstance(src_data, str):
            object_data.close()
    return True


def main():
    """Exercise put_object()"""

    # Assign these values before running the program
    test_bucket_name = 'BUCKET_NAME'
    test_object_name = 'OBJECT_NAME'
    filename = 'C:\\path\\to\\file.ext'
    # Alternatively, specify object contents using bytes.
    # filename = b'This is the data to store in the S3 object.'

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Put the object into the bucket
    success = put_object(test_bucket_name, test_object_name, filename)
    if success:
        logging.info(f'Added {test_object_name} to {test_bucket_name}')


if __name__ == '__main__':
    main()
