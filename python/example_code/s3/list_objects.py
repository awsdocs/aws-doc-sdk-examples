# snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
# snippet-sourcedescription:[list_objects.py lists the objects in an Amazon S3 bucket.]
# snippet-service:[s3]
# snippet-keyword:[Amazon S3]
# snippet-keyword:[Python]
# snippet-sourcesyntax:[python]
# snippet-sourcesyntax:[python]
# snippet-keyword:[Code Sample]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-2-13]
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
"""Lists the items in an Amazon S3 bucket"""
import logging
import sys
import boto3
from botocore.exceptions import ClientError


def list_bucket_objects(bucket_name):
    """List the objects in an Amazon S3 bucket

    :param bucket_name: string
    :return: List of bucket objects. If error, return None.
    """

    # Retrieve the list of bucket objects
    s_3 = boto3.client('s3')
    try:
        response = s_3.list_objects_v2(Bucket=bucket_name)
    except ClientError as e:
        # AllAccessDisabled error == bucket not found
        logging.error(e)
        return None

    # Only return the contenst if we found some keys
    if response['KeyCount'] > 0:
        return response['Contents']

    return None

def main():
    """Exercise list_bucket_objects()"""

    # Make sure we get a bucket name from the command line
    arguments = len(sys.argv) - 1

    if arguments < 1:
        print("You must supply a bucket name")
        return

    # Assign this value before running the program
    bucket_name = sys.argv[1]

    # Set up logging
    logging.basicConfig(level=logging.INFO,
                        format='%(message)s')

    # Retrieve the bucket's objects
    objects = list_bucket_objects(bucket_name)
    if objects is not None:
        # List the object names
        logging.info(f'Objects in {bucket_name}')
        for obj in objects:
            logging.info(f'  {obj["Key"]}')
    else:
        # Didn't get any keys
        logging.info(f'No objects in {bucket_name}')

if __name__ == '__main__':
    main()
