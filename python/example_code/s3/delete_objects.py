# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[delete_objects.py demonstrates how to delete multiple objects from an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-12]
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


def delete_objects(bucket_name, object_names):
    """Delete multiple objects from an Amazon S3 bucket

    :param bucket_name: string
    :param object_names: list of strings
    :return: True if the referenced objects were deleted, otherwise False
    """

    # Convert list of object names to appropriate data format
    objlist = [{'Key': obj} for obj in object_names]

    # Delete the objects
    s3 = boto3.client('s3')
    try:
        s3.delete_objects(Bucket=bucket_name, Delete={'Objects': objlist})
    except ClientError as e:
        logging.error(e)
        return False
    return True


def main():
    """Exercise delete_objects()"""

    # Assign these values before running the program
    test_bucket_name = 'BUCKET_NAME'
    test_object_names = ['OBJECT_NAME_01', 'OBJECT_NAME_02']

    # Set up logging
    logging.basicConfig(level=logging.DEBUG,
                        format='%(levelname)s: %(asctime)s: %(message)s')

    # Delete the objects
    if delete_objects(test_bucket_name, test_object_names):
        logging.info(f'Multiple objects were deleted from {test_bucket_name}')


if __name__ == '__main__':
    main()
